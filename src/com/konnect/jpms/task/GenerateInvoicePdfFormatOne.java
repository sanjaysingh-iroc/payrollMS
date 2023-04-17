package com.konnect.jpms.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author workrig
 *
 */
public class GenerateInvoicePdfFormatOne extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	HttpSession session;
	String invDate;
	String paycycle;
	int pro_id;
	String invoice_id;
	String operation;
	int teamsize;
	String strSessionEmpId;
	CommonFunctions CF; 
	String type;
	
	String proType;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		String currType = getCurrencyType(uF,getInvoice_id());
		String proCurrType = getProCurrencyType(uF,getPro_id());
		
		
//		System.out.println("getType() ===>> " + getType());
//		System.out.println("proCurrType ===>> " + proCurrType +"  currType ====>> " + currType );
		if (uF.parseToInt(getType()) == ADHOC_INVOICE) {
			if (currType != null && currType.equals("3")) {
				generateProjectAdHocPdfReport(uF);
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					request.setAttribute(PAGE, "/jsp/task/GeneratedInvoicePreview.jsp");
					request.setAttribute(TITLE, "Invoice Preview");
					return VIEW;
				} else if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
					session.setAttribute(MESSAGE, SUCCESSM+"Mail sent successfully"+END);
					return "mail";
				}
			} else {
				generateProjectAdHocPdfReportOtherCurr(uF);
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					request.setAttribute(PAGE, "/jsp/task/GeneratedInvoicePreview.jsp");
					request.setAttribute(TITLE, "Invoice Preview");
					return VIEW;
				} else if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
					session.setAttribute(MESSAGE, SUCCESSM+"Mail sent successfully"+END);
					return "mail";
				}
			}
		} else if (uF.parseToInt(getType()) == ADHOC_PRORETA_INVOICE) {
			if (currType != null && currType.equals("3")) {
				generateProjectProRetaPdfReport(uF);
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					request.setAttribute(PAGE, "/jsp/task/GeneratedInvoicePreview.jsp");
					request.setAttribute(TITLE, "Invoice Preview");
					return VIEW;
				} else if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
					session.setAttribute(MESSAGE, SUCCESSM+"Mail sent successfully"+END);
					return "mail";
				}
			} else {
				generateProjectProRetaPdfReportOtherCurr(uF);
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					request.setAttribute(PAGE, "/jsp/task/GeneratedInvoicePreview.jsp");
					request.setAttribute(TITLE, "Invoice Preview");
					return VIEW;
				} else if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
					session.setAttribute(MESSAGE, SUCCESSM+"Mail sent successfully"+END);
					return "mail";
				}
			}
		} else if (uF.parseToInt(getType()) == PRORETA_INVOICE) {
			if (currType != null && currType.equals(proCurrType)) {
				generateProjectProRetaPdfReport(uF);
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					request.setAttribute(PAGE, "/jsp/task/GeneratedInvoicePreview.jsp");
					request.setAttribute(TITLE, "Invoice Preview");
					return VIEW;
				} else if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
					session.setAttribute(MESSAGE, SUCCESSM+"Mail sent successfully"+END);
					return "mail";
				}
			} else {
				generateProjectProRetaPdfReportOtherCurr(uF);
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					request.setAttribute(PAGE, "/jsp/task/GeneratedInvoicePreview.jsp");
					request.setAttribute(TITLE, "Invoice Preview");
					return VIEW;
				} else if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
					session.setAttribute(MESSAGE, SUCCESSM+"Mail sent successfully"+END);
					return "mail";
				}
			}
		} else {
			if (currType != null && currType.equals(proCurrType)) {
				generateProjectPdfReport(uF);
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					request.setAttribute(PAGE, "/jsp/task/GeneratedInvoicePreview.jsp");
					request.setAttribute(TITLE, "Invoice Preview");
					return VIEW;
				} else if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
					session.setAttribute(MESSAGE, SUCCESSM+"Mail sent successfully"+END);
					return "mail";
				}
			} else {
				generateProjectPdfReportOtherCurr(uF);
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					request.setAttribute(PAGE, "/jsp/task/GeneratedInvoicePreview.jsp");
					request.setAttribute(TITLE, "Invoice Preview");
					return VIEW;
				} else if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
					session.setAttribute(MESSAGE, SUCCESSM+"Mail sent successfully"+END);
					return "mail";
				}
			}
			session.removeAttribute("pro_id");
		} 
		return null;
	}
	
	private void generateProjectAdHocPdfReportOtherCurr(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmAccNoBankName = CF.getBankAccNoMap(con, uF);
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			// Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);

			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);

			pst = con.prepareStatement("select * from client_details");
			rs = pst.executeQuery();
			Map<String, String> hmAdhocClientDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmAdhocClientDetails.put(rs.getString("client_id"), rs.getString("client_name"));
				hmAdhocClientDetails.put(rs.getString("client_id") + "_TYPE", rs.getString("client_type"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_details where  promntc_invoice_id=?");
			pst.setInt(1, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			while (rs.next()) {
				
				hmInvoiceDetails.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoiceDetails.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmInvoiceDetails.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("REFERENCE_NO_DESC", rs.getString("reference_no_desc"));
				hmInvoiceDetails.put("PROJECT_ID", rs.getString("pro_id"));
				hmInvoiceDetails.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoiceDetails.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmInvoiceDetails.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmInvoiceDetails.put("SPOC_ID", rs.getString("spoc_id"));
				hmInvoiceDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmInvoiceDetails.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmInvoiceDetails.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInvoiceDetails.put("DEPART_ID", rs.getString("depart_id"));
				hmInvoiceDetails.put("INVOICE_AMOUNT", rs.getString("invoice_amount"));
				
//				if (uF.parseToDouble(inrValue) > 0) {
//					dblOCInvoiceAmt = uF.parseToDouble(rs.getString("invoice_amount")) / uF.parseToDouble(inrValue);
//					dblOCPartiTotAmt = uF.parseToDouble(rs.getString("particulars_total_amount")) / uF.parseToDouble(inrValue);
//					dblOCOtherAmt = uF.parseToDouble(rs.getString("other_amount")) / uF.parseToDouble(inrValue);
//				}
				hmInvoiceDetails.put("OC_INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
				hmInvoiceDetails.put("OC_PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));
				hmInvoiceDetails.put("OC_OTHER_AMOUNT", rs.getString("oc_other_amount"));

				hmInvoiceDetails.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("particulars_total_amount"));

				hmInvoiceDetails.put("OTHER_AMOUNT", rs.getString("other_amount"));
				hmInvoiceDetails.put("OTHER_PARTICULAR", rs.getString("other_particular"));
				hmInvoiceDetails.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
				hmInvoiceDetails.put("CURR_ID", rs.getString("curr_id"));
				hmInvoiceDetails.put("SERVICE_ID", rs.getString("service_id"));
				hmInvoiceDetails.put("CLIENT_NAME", uF.showData((String) hmAdhocClientDetails.get(rs.getString("client_id")), "N/A"));
				hmInvoiceDetails.put("CLIENT_TYPE", uF.showData((String) hmAdhocClientDetails.get(rs.getString("client_id") + "_TYPE"), "N/A"));

				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));
				hmInvoiceDetails.put("PRO_FREQ_ID", rs.getString("pro_freq_id"));
				hmInvoiceDetails.put("INVOICE_TEMPLATE_ID", rs.getString("invoice_template_id"));
				
				hmInvoiceDetails.put("BANK_ID", rs.getString("bank_branch_id"));
				hmInvoiceDetails.put("PRO_BANK_NAME", uF.showData(hmAccNoBankName.get(rs.getString("bank_branch_id")), ""));
				hmInvoiceDetails.put("PAYPAL_MAIL_ID", rs.getString("paypal_mail_id"));
				hmInvoiceDetails.put("ACCOUNT_REF", rs.getString("acc_ref"));
				hmInvoiceDetails.put("PO_NO", rs.getString("po_no"));
				hmInvoiceDetails.put("TERMS", rs.getString("terms"));
				hmInvoiceDetails.put("BILL_DUE_DATE", uF.getDateFormat(rs.getString("bill_due_date"), DBDATE, CF.getStrReportDateFormat()));

			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProjectOwnerDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod "
					+ "where eod.emp_id=epd.emp_per_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_OWNER_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProjectOwnerDetails.put("EMP_ID", rs.getString("emp_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmProjectOwnerDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmProjectOwnerDetails.put("EMP_ORG_ID", rs.getString("org_id"));
				hmProjectOwnerDetails.put("EMP_PAN_NO", rs.getString("emp_pan_no"));
				hmProjectOwnerDetails.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				hmProjectOwnerDetails.put("EMP_WORK_LOCATION", rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'PARTI' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<List<String>> outerAmtList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'OPE' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();

			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'TAX' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<List<String>> outerTaxList = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars_label"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				innerList.add(rs.getString("tax_percent"));
				outerTaxList.add(innerList);
			}
			rs.close();
			pst.close();
			

			pst = con.prepareStatement("select * from work_location_info where weightage > 0 order by weightage");
			rs = pst.executeQuery();
			String wLocation = "Offices at: ";
			int j = 0;
			while (rs.next()) {
				if (j == 0) {
					wLocation += rs.getString("wlocation_name");
				} else {
					wLocation += ", " + rs.getString("wlocation_name");
				}
				j++;
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMapForBilling(con);
			if (hmWorkLocation == null)
				hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmWlocation = hmWorkLocation.get(hmProjectOwnerDetails.get("EMP_WORK_LOCATION"));
			
			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd  where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmProjectDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProjectDetails.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PRIORITY", rs.getString("priority"));
				hmProjectDetails.put("DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("ACTIVITY", rs.getString("activity"));
				hmProjectDetails.put("SERVICE", rs.getString("service"));
				hmProjectDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmProjectDetails.put("DEPARTMENT_ID", rs.getString("department_id"));
				hmProjectDetails.put("PRO_CURR_ID", rs.getString("billing_curr_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");
			pst.setInt(1, getPro_id());
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData.put("CLIENT_ID", rs.getString("client_id"));
				hmProjectData.put("ORG_ID", rs.getString("org_id"));
				hmProjectData.put("WLOCATION_ID", rs.getString("wlocation_id"));
			//===start parvez date: 17-10-2022===	
//				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owner"));
				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owners"));
			//===end parvez date: 17-10-2022===	
				hmProjectData.put("PROJECT_CURRENCY", rs.getString("billing_curr_id"));
				hmProjectData.put("CLIENT_SPOC", rs.getString("poc"));
				hmProjectData.put("INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
				
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF, CF.getEmpOrgId(con, uF, hmInvoiceDetails.get("PRO_OWNER_ID")));
			if(hmOrgData==null) hmOrgData = new HashMap<String, String>();

			pst = con.prepareStatement("select * from client_details where client_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("CLIENT_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(", ", ",\n") : "";
				// client_address=rs.getString("client_address");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
			rs = pst.executeQuery();
			String client_poc = "";
			while (rs.next()) {
				client_poc = uF.showData(rs.getString("contact_fname"), "")+" "+ uF.showData(rs.getString("contact_mname"), "") +" "+ uF.showData(rs.getString("contact_lname"), "");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select bd1.bank_name as branch_bank_name,bd.* from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id and " +
					"bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("branch_bank_name"));
				hmBankBranch.put("BRANCH_ID", rs.getString("branch_id"));
				hmBankBranch.put("BRANCH_CODE", rs.getString("branch_code"));
				hmBankBranch.put("BRANCH_DESCRIPTION", rs.getString("bank_description"));
				hmBankBranch.put("BRANCH_ADDRESS", rs.getString("bank_address"));
				hmBankBranch.put("BRANCH_CITY", rs.getString("bank_city"));
				hmBankBranch.put("BRANCH_STATE_ID", rs.getString("bank_state_id"));
				hmBankBranch.put("BRANCH_COUNTRY_ID", rs.getString("bank_country_id"));
				hmBankBranch.put("BRANCH_BRANCH", rs.getString("bank_branch"));
				hmBankBranch.put("BRANCH_EMAIL", rs.getString("bank_email"));
				hmBankBranch.put("BRANCH_FAX", rs.getString("bank_fax"));
				hmBankBranch.put("BRANCH_CONTACT", rs.getString("bank_contact"));
				hmBankBranch.put("OTHER_INFO", rs.getString("other_information"));
				if(uF.parseToBoolean(rs.getString("is_ifsc"))) {
					hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_swift"))) {
					hmBankBranch.put("BRANCH_SWIFT_CODE", rs.getString("swift_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_clearing_code"))) {
					hmBankBranch.put("BRANCH_CLEARING_CODE", rs.getString("bank_clearing_code"));
				}
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();

			ByteArrayOutputStream buffer = generateAdhocPdfDocumentOtherCurr(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, 
					hmDesignation, wLocation, client_address, outerAmtList, outerTaxList, client_poc, hmBankBranch, hmCurrencyDetails, 
					hmWlocation, hmProjectDetails, hmOrgData);
			if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
				byte[] bytes = buffer.toByteArray();
				List<String> empList = new ArrayList<String>();
			//===start parvez date: 17-10-2022===	
				/*if(uF.parseToInt(hmProjectData.get("PROJECT_OWNER")) > 0){
					empList.add(hmProjectData.get("PROJECT_OWNER"));
				}*/
				List<String> arrPartnersIds = null;
				if(hmProjectData.get("PROJECT_OWNER") != null){
	    			arrPartnersIds = Arrays.asList(hmProjectData.get("PROJECT_OWNER").split(","));
	    		}
				
				for(int ii=1; arrPartnersIds!=null && ii<arrPartnersIds.size(); ii++){
					if(uF.parseToInt(arrPartnersIds.get(ii)) > 0){
						empList.add(arrPartnersIds.get(ii));
					}
				}
			//===end parvez date: 17-10-2022===	
				
				pst = con.prepareStatement("SELECT eod.emp_id FROM user_details ud, employee_official_details eod WHERE ud.usertype_id = 4 and ud.emp_id = eod.emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !empList.contains(rs.getString("emp_id"))){
						empList.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_PAYMENT_ALERT, CF);
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);

				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
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
					nF.setStrProjectFreqName("Add-Hoc");
					nF.setStrFromDate("");
					nF.setStrToDate("");
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
					nF.setPdfData(bytes);
					nF.setStrAttachmentFileName("AdHoc_Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
					nF.sendNotifications(); 
				}
				
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
					Map<String, String> hmEmpDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from employee_personal_details epd where epd.emp_per_id=? ");
					pst.setInt(1, uF.parseToInt(empList.get(i)));
					rs = pst.executeQuery();
					boolean empFlag = false;
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("emp_per_id"))> 0) {
							hmEmpDetails.put(rs.getString("emp_per_id")+"_FNAME", rs.getString("emp_fname"));
							hmEmpDetails.put(rs.getString("emp_per_id")+"_LNAME", rs.getString("emp_lname"));
							if(rs.getString("emp_email_sec") !=null && rs.getString("emp_email_sec").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email_sec"));
							} else if(rs.getString("emp_email") !=null && rs.getString("emp_email").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email"));
							}
							hmEmpDetails.put(rs.getString("emp_per_id")+"_CONTACT_NO", rs.getString("emp_contactno_mob"));
							
							empFlag = true;
						}
					}
					rs.close();
					pst.close();
					
					if(empFlag){
						nF.setStrCustFName(hmEmpDetails.get(empList.get(i)+"_FNAME"));
						nF.setStrCustLName(hmEmpDetails.get(empList.get(i)+"_LNAME"));
						nF.setStrEmpMobileNo(hmEmpDetails.get(empList.get(i)+"_CONTACT_NO"));
						nF.setStrEmpEmail(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						nF.setStrEmailTo(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrProjectFreqName("Add-Hoc");
						nF.setStrFromDate("");
						nF.setStrToDate("");
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
						nF.setPdfData(bytes);
						nF.setStrAttachmentFileName("AdHoc_Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
						nF.sendNotifications();
					}
				}
				
			}else if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
				String directory = CF.getStrDocSaveLocation()+I_TEMP+"/"; 
				FileUtils.forceMkdir(new File(directory));
				
				byte[] bytes = buffer.toByteArray();
				File f = File.createTempFile("tmp", ".pdf", new File(directory));
				FileOutputStream fileOuputStream = new FileOutputStream(f); 
				fileOuputStream.write(bytes);
				
				String filePath = CF.getStrDocRetriveLocation()+I_TEMP+"/"+f.getName();
				request.setAttribute("filePath",filePath);
				
			} else if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=AdHoc_Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private ByteArrayOutputStream generateAdhocPdfDocumentOtherCurr(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails,
			Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String wLocation,
			String client_address, List<List<String>> outerAmtList, List<List<String>> outerTaxList, String client_poc, Map<String, String> hmBankBranch,
			Map<String, Map<String, String>> hmCurrencyDetails, Map<String, String> hmWlocation, Map<String, String> hmProjectDetails,
			Map<String, String> hmOrgData) {
//		System.out.println("generateAdhocPdfDocumentOtherCurr ======");
		
//		Font font = FontFactory.getFont("/fonts/Calibri.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		Font font = FontFactory.getFont("/fonts/arial.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		BaseFont baseFont = font.getBaseFont();
		Font heading = new Font(baseFont, 13);
		Font normal = new Font(baseFont, 11);
		Font normal1 = new Font(baseFont, 9);
		Font normalwithbold = new Font(baseFont, 14, Font.BOLD);
		Font small = new Font(baseFont, 8);
		Font small1 = new Font(baseFont, 9);
		Font smallBold = new Font(baseFont, 9, Font.BOLD);
		Font italicEffect = new Font(baseFont, 9, Font.ITALIC);
		Font smallBoldWhite = new Font(baseFont, 8, Font.BOLD, BaseColor.WHITE);
		BaseColor greenColor = WebColors.getRGBColor("#3B9C9C");
		BaseColor backgroundColor = WebColors.getRGBColor("#F0F0F0");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			
			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null) hmCurr = new HashMap<String, String>();
			String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";
			
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);
			
			//New Row
			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			String orgLogo = "";
			if(hmOrgData.get("ORG_LOGO")!=null && !hmOrgData.get("ORG_LOGO").trim().equals("")){
//				orgLogo = "<img src='"+CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO") +"' height=\"40\" width=\"160\">";
				orgLogo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO");
			}
//			List<Element> al = HTMLWorker.parseToList(new StringReader(orgLogo), null);
//			Paragraph pr = new Paragraph("",small);
//			pr.addAll(al);
//			row1 =new PdfPCell(new Paragraph(pr));
			FileInputStream fileInputStream=null;
	        File file = new File(orgLogo);
	        byte[] bFile = new byte[(int) file.length()];
	        fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    Image imageLogo = Image.getInstance(bFile);
			row1 =new PdfPCell(imageLogo,true);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER); 
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);  
			
			
			String orgAdd = hmOrgData.get("ORG_ADDRESS") != null ? hmOrgData.get("ORG_ADDRESS").replace(", ", ",\n") + "- "
					+ uF.showData(hmOrgData.get("ORG_PINCODE"), "") : "";
			String orgAddress = "<p><strong><span style=\"font-size:10px\">"+uF.showData(hmOrgData.get("ORG_NAME"), "")+"</span> </strong>" +
					"<div><span style=\"font-size:9px\">"+orgAdd+"</span></div></p>";
			List<Element> al = HTMLWorker.parseToList(new StringReader(orgAddress), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("Invoice", normalwithbold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			//New Row
			//first
			PdfPTable table2 = new PdfPTable(1);
			table2.setWidthPercentage(100);
			
			PdfPCell row12 = new PdfPCell(new Paragraph("Invoice To",smallBoldWhite));
			row12.setHorizontalAlignment(Element.ALIGN_CENTER);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(greenColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
//			String strClientPoc = "<p>"+uF.showData(client_poc, "")+"<div>"+client_address+"</div></p>";
//			al = HTMLWorker.parseToList(new StringReader(strClientPoc), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al);
//			row12 = new PdfPCell(new Paragraph(pr));
			String strClientPoc = uF.showData(client_poc, "")+"\n"+hmInvoiceDetails.get("CLIENT_NAME")+"\n"+uF.showData(client_address, "");
			row12 = new PdfPCell(new Paragraph(strClientPoc,small));
			row12.setHorizontalAlignment(Element.ALIGN_LEFT);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(backgroundColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
			//second
			PdfPTable table3 = new PdfPTable(1);
			table3.setWidthPercentage(100);
			
			PdfPCell row13 = new PdfPCell(new Paragraph("",small));
			row13.setHorizontalAlignment(Element.ALIGN_CENTER);
			row13.setBorder(Rectangle.NO_BORDER);
			row13.setPadding(2.5f);
			table3.addCell(row13);
			
			
			//third
			PdfPTable table4 = new PdfPTable(2);
			table4.setWidthPercentage(100);
			
			PdfPCell row14 = new PdfPCell(new Paragraph("Date",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph("Invoice No.",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			//third new row
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_CODE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_LEFT);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			
			
			PdfPTable table1 = new PdfPTable(3);
			table1.setWidthPercentage(100);
			
			PdfPCell row11 = new PdfPCell(table2);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table3);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table4);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row1 = new PdfPCell(table1);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TEMPLATE_ID")) == 1) {
				//New Row
				//first
				PdfPTable table5 = new PdfPTable(5);
				table5.setWidthPercentage(100);
				
				PdfPCell row5 = new PdfPCell(new Paragraph("Account Ref.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("P.O. No.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Terms",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Due Date",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Project Name",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				//second 
				row5 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("ACCOUNT_REF"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("PO_NO"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("TERMS"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("BILL_DUE_DATE"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PRO_NAME"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row1 = new PdfPCell(table5);
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table6 = new PdfPTable(4);
//			table6.setWidthPercentage(100);
			float[] columnWidths = new float[] {6f, 64f, 15f, 15f};
	        table6.setWidths(columnWidths);
			
			PdfPCell row6 = new PdfPCell(new Paragraph("#",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Description",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row1.setPadding(2.5f);
			table6.addCell(row6);
			
			//second 
			
			int pertiSize = 0;
			
			for (int i = 0,k=1; outerAmtList != null && i < outerAmtList.size(); i++) {
				List<String> innerList = outerAmtList.get(i);
				if (innerList == null) innerList = new ArrayList<String>();

				if (uF.parseToDouble(innerList.get(2)) == 0.0) {
					continue;
				}
			
				row6 = new PdfPCell(new Paragraph(k+". ",small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""),small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3))),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				pertiSize++;
				k++;
			}
			
			//second new row
			
			String strLines = "";
			for (int i = pertiSize; i < 27; i++) {
				strLines = strLines + "\n";
			}
			
			row6 = new PdfPCell(new Paragraph(strLines,small));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(backgroundColor);
			row6.setColspan(4);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			
			row1 = new PdfPCell(table6);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table7 = new PdfPTable(6);
			table7.setWidthPercentage(100);
			
			PdfPCell row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Subtotal",smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//second
			row7 = new PdfPCell(new Paragraph("Other Fees / Taxes",smallBoldWhite));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(greenColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//third
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				
				row7 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), ""),small));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setColspan(3);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph("",smallBold));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
			}
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Total"+uF.showData(currency, ""),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//fifth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			double totalAmt=uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"));
			String digitTotal="";
	        String strTotalAmt=""+totalAmt;
	        if(strTotalAmt.contains(".")){
	        	strTotalAmt=strTotalAmt.replace(".", ",");
	        	String[] temp=strTotalAmt.split(",");
	        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
	        	if(uF.parseToInt(temp[1])>0){
	        		int pamt=0;
	        		if(temp[1].length()==1){
	        			pamt=uF.parseToInt(temp[1]+"0");
	        		}else{
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" "+uF.showData(hmCurr.get("CURR_SUB_DIVISION"), ""); 
	        	}
	        }else{
	        	int totalAmt1=(int)totalAmt;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
			row7 = new PdfPCell(new Paragraph(""+digitTotal,small));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row1 = new PdfPCell(table7);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			if(uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")) > 0) {
				//New Row
				row1 = new PdfPCell(new Paragraph("Payment Mode:", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row       
				StringBuilder sbBankAccData = new StringBuilder();
				StringBuilder sbBankAccData1 = new StringBuilder();
				StringBuilder sbBankAccData2 = new StringBuilder();
				sbBankAccData.append("Bank Details: \n");
				sbBankAccData.append("A/C No.: "+hmBankBranch.get("BRANCH_ACCOUNT_NO")+"\n");
				sbBankAccData.append("Branch: "+hmBankBranch.get("BRANCH_BRANCH")+"\n");
				sbBankAccData.append("Bank: "+hmBankBranch.get("BRANCH_BANK_NAME")+"\n");
				sbBankAccData1.append("\n");
				if(hmBankBranch.get("BRANCH_IFSC_CODE") != null && !hmBankBranch.get("BRANCH_IFSC_CODE").equals("")) {
					sbBankAccData1.append("IFSC: "+hmBankBranch.get("BRANCH_IFSC_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_SWIFT_CODE") != null && !hmBankBranch.get("BRANCH_SWIFT_CODE").equals("")) {
					sbBankAccData1.append("SWIFT: "+hmBankBranch.get("BRANCH_SWIFT_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_CLEARING_CODE") != null && !hmBankBranch.get("BRANCH_CLEARING_CODE").equals("")) {
					sbBankAccData1.append("BCC: "+hmBankBranch.get("BRANCH_CLEARING_CODE")+"\n");
				}
				sbBankAccData2.append("\n");
				if(hmBankBranch.get("OTHER_INFO") != null && !hmBankBranch.get("OTHER_INFO").equals("")) {
					sbBankAccData2.append(hmBankBranch.get("OTHER_INFO")+"\n");
				}
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData1.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData2.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
			}
			
			//New Row
			if(hmInvoiceDetails.get("PAYPAL_MAIL_ID") !=null && !hmInvoiceDetails.get("PAYPAL_MAIL_ID").trim().equals("")){
				row1 = new PdfPCell(new Paragraph("Paypal- "+uF.showData(hmInvoiceDetails.get("PAYPAL_MAIL_ID"), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f); 
				table.addCell(row1);
			}
			
			//New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setIndent(10.0f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("\n\nAuthorised Signatory,", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("This is computer generated statement and does not need signature.", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table8 = new PdfPTable(5);
			table8.setWidthPercentage(100);
			
			PdfPCell row8 = new PdfPCell(new Paragraph("Phone No.",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("E-mail",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("Web Site",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			
			
			//second 
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_CONTACT"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_EMAIL"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_WEBSITE"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row1 = new PdfPCell(table8);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			/*String strPath = request.getRealPath("/images1/icons/icons/taskrig.png");
			System.out.println("request.getRealPath()======>"+request.getRealPath("/images1/icons/icons/taskrig.png"));
			System.out.println("request.getContextPath()======>"+request.getContextPath()+"/images1/icons/icons/taskrig.png");
			String strPoweredBy = "Powered by <img src='"+strPath+"' height=\"25\">";
			List<Element> al2 = HTMLWorker.parseToList(new StringReader(strPoweredBy), null);
			Paragraph pr2 = new Paragraph("",small);
			pr2.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr2));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);*/
			
			document.add(table);
			
//			Image imageProductLogo=Image.getInstance(request.getRealPath("/images1/icons/icons/taskrig.png"));
//			imageProductLogo.setAbsolutePosition(445, 0);
//			imageProductLogo.scaleToFit(100, 100);
//			document.add(imageProductLogo);
			
			document.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	private void generateProjectAdHocPdfReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmAccNoBankName = CF.getBankAccNoMap(con, uF);
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			// Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);

			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);

			pst = con.prepareStatement("select * from client_details");
			rs = pst.executeQuery();
			Map<String, String> hmAdhocClientDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmAdhocClientDetails.put(rs.getString("client_id"), rs.getString("client_name"));
				hmAdhocClientDetails.put(rs.getString("client_id") + "_TYPE", rs.getString("client_type"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_details where  promntc_invoice_id=?");
			pst.setInt(1, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmInvoiceDetails.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoiceDetails.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmInvoiceDetails.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("REFERENCE_NO_DESC", rs.getString("reference_no_desc"));
				hmInvoiceDetails.put("PROJECT_ID", rs.getString("pro_id"));
				hmInvoiceDetails.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoiceDetails.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmInvoiceDetails.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmInvoiceDetails.put("SPOC_ID", rs.getString("spoc_id"));
				hmInvoiceDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmInvoiceDetails.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmInvoiceDetails.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInvoiceDetails.put("DEPART_ID", rs.getString("depart_id"));
				hmInvoiceDetails.put("INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
				hmInvoiceDetails.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));

				hmInvoiceDetails.put("OTHER_AMOUNT", rs.getString("oc_other_amount"));
				hmInvoiceDetails.put("OTHER_PARTICULAR", rs.getString("other_particular"));
				hmInvoiceDetails.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
				hmInvoiceDetails.put("CURR_ID", rs.getString("curr_id"));
				hmInvoiceDetails.put("SERVICE_ID", rs.getString("service_id"));
				hmInvoiceDetails.put("CLIENT_NAME", uF.showData((String) hmAdhocClientDetails.get(rs.getString("client_id")), "N/A"));
				hmInvoiceDetails.put("CLIENT_TYPE", uF.showData((String) hmAdhocClientDetails.get(rs.getString("client_id") + "_TYPE"), "N/A"));

				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));
				
				hmInvoiceDetails.put("PRO_FREQ_ID", rs.getString("pro_freq_id"));
				hmInvoiceDetails.put("INVOICE_TEMPLATE_ID", rs.getString("invoice_template_id"));
				
				hmInvoiceDetails.put("BANK_ID", rs.getString("bank_branch_id"));
				hmInvoiceDetails.put("PRO_BANK_NAME", uF.showData(hmAccNoBankName.get(rs.getString("bank_branch_id")), ""));
				hmInvoiceDetails.put("PAYPAL_MAIL_ID", rs.getString("paypal_mail_id"));
				hmInvoiceDetails.put("ACCOUNT_REF", rs.getString("acc_ref"));
				hmInvoiceDetails.put("PO_NO", rs.getString("po_no"));
				hmInvoiceDetails.put("TERMS", rs.getString("terms"));
				hmInvoiceDetails.put("BILL_DUE_DATE", uF.getDateFormat(rs.getString("bill_due_date"), DBDATE, CF.getStrReportDateFormat()));

			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProjectOwnerDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod "
					+ "where eod.emp_id=epd.emp_per_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_OWNER_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProjectOwnerDetails.put("EMP_ID", rs.getString("emp_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmProjectOwnerDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmProjectOwnerDetails.put("EMP_ORG_ID", rs.getString("org_id"));
				hmProjectOwnerDetails.put("EMP_PAN_NO", rs.getString("emp_pan_no"));
				hmProjectOwnerDetails.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				hmProjectOwnerDetails.put("EMP_WORK_LOCATION", rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'PARTI' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<List<String>> outerAmtList = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'OPE' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();

			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'TAX' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<List<String>> outerTaxList = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars_label"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				innerList.add(rs.getString("tax_percent"));
				outerTaxList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from work_location_info where weightage > 0 order by weightage");
			rs = pst.executeQuery();
			String wLocation = "Offices at: ";
			int j = 0;
			while (rs.next()) {
				if (j == 0) {
					wLocation += rs.getString("wlocation_name");
				} else {
					wLocation += ", " + rs.getString("wlocation_name");
				}
				j++;
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMapForBilling(con);
			if (hmWorkLocation == null)
				hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmWlocation = hmWorkLocation.get(hmProjectOwnerDetails.get("EMP_WORK_LOCATION"));

			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd  where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmProjectDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProjectDetails.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PRIORITY", rs.getString("priority"));
				hmProjectDetails.put("DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("ACTIVITY", rs.getString("activity"));
				hmProjectDetails.put("SERVICE", rs.getString("service"));
				hmProjectDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmProjectDetails.put("DEPARTMENT_ID", rs.getString("department_id"));
				hmProjectDetails.put("PRO_CURR_ID", rs.getString("billing_curr_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData.put("CLIENT_ID", rs.getString("client_id"));
				hmProjectData.put("ORG_ID", rs.getString("org_id"));
				hmProjectData.put("WLOCATION_ID", rs.getString("wlocation_id"));
			//===start parvez date: 17-10-2022===	
//				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owner"));
				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owners"));
			//===end parvez date: 17-10-2022===	
				hmProjectData.put("PROJECT_CURRENCY", rs.getString("billing_curr_id"));
				hmProjectData.put("CLIENT_SPOC", rs.getString("poc"));
				hmProjectData.put("INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF,CF.getEmpOrgId(con, uF, hmInvoiceDetails.get("PRO_OWNER_ID")));
			if(hmOrgData==null) hmOrgData = new HashMap<String, String>();

			pst = con.prepareStatement("select * from client_details where client_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("CLIENT_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(", ", ",\n") : "";
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
			rs = pst.executeQuery();
			String client_poc = "";
			while (rs.next()) {
				client_poc = uF.showData(rs.getString("contact_fname"), "")+" "+ uF.showData(rs.getString("contact_mname"), "") +" "+ uF.showData(rs.getString("contact_lname"), "");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select bd1.bank_name as branch_bank_name,bd.* from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id and " +
					"bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("branch_bank_name"));
				hmBankBranch.put("BRANCH_ID", rs.getString("branch_id"));
				hmBankBranch.put("BRANCH_CODE", rs.getString("branch_code"));
				hmBankBranch.put("BRANCH_DESCRIPTION", rs.getString("bank_description"));
				hmBankBranch.put("BRANCH_ADDRESS", rs.getString("bank_address"));
				hmBankBranch.put("BRANCH_CITY", rs.getString("bank_city"));
				hmBankBranch.put("BRANCH_STATE_ID", rs.getString("bank_state_id"));
				hmBankBranch.put("BRANCH_COUNTRY_ID", rs.getString("bank_country_id"));
				hmBankBranch.put("BRANCH_BRANCH", rs.getString("bank_branch"));
				hmBankBranch.put("BRANCH_EMAIL", rs.getString("bank_email"));
				hmBankBranch.put("BRANCH_FAX", rs.getString("bank_fax"));
				hmBankBranch.put("BRANCH_CONTACT", rs.getString("bank_contact"));
				hmBankBranch.put("OTHER_INFO", rs.getString("other_information"));
				if(uF.parseToBoolean(rs.getString("is_ifsc"))) {
					hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_swift"))) {
					hmBankBranch.put("BRANCH_SWIFT_CODE", rs.getString("swift_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_clearing_code"))) {
					hmBankBranch.put("BRANCH_CLEARING_CODE", rs.getString("bank_clearing_code"));
				}
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();

			ByteArrayOutputStream buffer = generateAdhocPdfDocument(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, hmDesignation, 
					wLocation, client_address, outerAmtList, outerTaxList, client_poc, hmBankBranch, hmCurrencyDetails, hmWlocation,
					hmProjectDetails,hmOrgData);
			if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
				byte[] bytes = buffer.toByteArray();
				List<String> empList = new ArrayList<String>();
			//===start parvez date: 17-10-2022===	
				/*if(uF.parseToInt(hmProjectData.get("PROJECT_OWNER")) > 0){
					empList.add(hmProjectData.get("PROJECT_OWNER"));
				}*/
				
				List<String> arrPartnersIds = null;
				if(hmProjectData.get("PROJECT_OWNER") != null){
	    			arrPartnersIds = Arrays.asList(hmProjectData.get("PROJECT_OWNER").split(","));
	    		}
				
				for(int ii=1; arrPartnersIds!=null && ii<arrPartnersIds.size(); ii++){
					if(uF.parseToInt(arrPartnersIds.get(ii)) > 0){
						empList.add(arrPartnersIds.get(ii));
					}
				}
			//===end parvez date: 17-10-2022===	
				
				pst = con.prepareStatement("SELECT eod.emp_id FROM user_details ud, employee_official_details eod WHERE ud.usertype_id = 4 and ud.emp_id = eod.emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !empList.contains(rs.getString("emp_id"))){
						empList.add(rs.getString("emp_id"));
					}
				}
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_PAYMENT_ALERT, CF);
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);

				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
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
					nF.setStrProjectFreqName("Add-Hoc");
					nF.setStrFromDate("");
					nF.setStrToDate("");
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
					nF.setPdfData(bytes);
					nF.setStrAttachmentFileName("AdHoc_Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
					nF.sendNotifications(); 
				}
				
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
					Map<String, String> hmEmpDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from employee_personal_details epd where epd.emp_per_id=? ");
					pst.setInt(1, uF.parseToInt(empList.get(i)));
					rs = pst.executeQuery();
					boolean empFlag = false;
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("emp_per_id"))> 0) {
							hmEmpDetails.put(rs.getString("emp_per_id")+"_FNAME", rs.getString("emp_fname"));
							hmEmpDetails.put(rs.getString("emp_per_id")+"_LNAME", rs.getString("emp_lname"));
							if(rs.getString("emp_email_sec") !=null && rs.getString("emp_email_sec").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email_sec"));
							} else if(rs.getString("emp_email") !=null && rs.getString("emp_email").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email"));
							}
							hmEmpDetails.put(rs.getString("emp_per_id")+"_CONTACT_NO", rs.getString("emp_contactno_mob"));
							
							empFlag = true;
						}
					}
					rs.close();
					pst.close();
					
					if(empFlag){
						nF.setStrCustFName(hmEmpDetails.get(empList.get(i)+"_FNAME"));
						nF.setStrCustLName(hmEmpDetails.get(empList.get(i)+"_LNAME"));
						nF.setStrEmpMobileNo(hmEmpDetails.get(empList.get(i)+"_CONTACT_NO"));
						nF.setStrEmpEmail(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						nF.setStrEmailTo(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrProjectFreqName("Add-Hoc");
						nF.setStrFromDate("");
						nF.setStrToDate("");
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
						nF.setPdfData(bytes);
						nF.setStrAttachmentFileName("AdHoc_Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
						nF.sendNotifications();
					}
				}
				
			}else if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
				String directory = CF.getStrDocSaveLocation()+I_TEMP+"/"; 
				FileUtils.forceMkdir(new File(directory));
				
				byte[] bytes = buffer.toByteArray();
				File f = File.createTempFile("tmp", ".pdf", new File(directory));
				FileOutputStream fileOuputStream = new FileOutputStream(f); 
				fileOuputStream.write(bytes);
				
				String filePath = CF.getStrDocRetriveLocation()+I_TEMP+"/"+f.getName();
				request.setAttribute("filePath",filePath);
				
			} else if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=AdHoc_Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private ByteArrayOutputStream generateAdhocPdfDocument(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails,
			Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String wLocation,
			String client_address, List<List<String>> outerAmtList, List<List<String>> outerTaxList, String client_poc, Map<String, String> hmBankBranch,
			Map<String, Map<String, String>> hmCurrencyDetails, Map<String, String> hmWlocation, Map<String, String> hmProjectDetails,
			Map<String, String> hmOrgData) {
//		System.out.println("generatePdfDocument ======");
		
//		Font font = FontFactory.getFont("/fonts/Calibri.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		Font font = FontFactory.getFont("/fonts/arial.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		BaseFont baseFont = font.getBaseFont();
		Font heading = new Font(baseFont, 13);
		Font normal = new Font(baseFont, 11);
		Font normal1 = new Font(baseFont, 9);
		Font normalwithbold = new Font(baseFont, 14, Font.BOLD);
		Font small = new Font(baseFont, 8);
		Font small1 = new Font(baseFont, 9);
		Font smallBold = new Font(baseFont, 9, Font.BOLD);
		Font italicEffect = new Font(baseFont, 9, Font.ITALIC);
		Font smallBoldWhite = new Font(baseFont, 8, Font.BOLD, BaseColor.WHITE);
		BaseColor greenColor = WebColors.getRGBColor("#3B9C9C");
		BaseColor backgroundColor = WebColors.getRGBColor("#F0F0F0");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			
			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null) hmCurr = new HashMap<String, String>();
			String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";
			
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);
			
			//New Row
			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			String orgLogo = "";
			if(hmOrgData.get("ORG_LOGO")!=null && !hmOrgData.get("ORG_LOGO").trim().equals("")){
//				orgLogo = "<img src='"+CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO") +"' height=\"40\" width=\"160\">";
				orgLogo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO");
			}
//			List<Element> al = HTMLWorker.parseToList(new StringReader(orgLogo), null);
//			Paragraph pr = new Paragraph("",small);
//			pr.addAll(al);
//			row1 =new PdfPCell(new Paragraph(pr));
			FileInputStream fileInputStream=null;
	        File file = new File(orgLogo);
	        byte[] bFile = new byte[(int) file.length()];
	        fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    Image imageLogo = Image.getInstance(bFile);
			row1 =new PdfPCell(imageLogo,true);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);  
			
			String orgAdd = hmOrgData.get("ORG_ADDRESS") != null ? hmOrgData.get("ORG_ADDRESS").replace(", ", ",\n") + "- "
					+ uF.showData(hmOrgData.get("ORG_PINCODE"), "") : "";
			String orgAddress = "<p><strong><span style=\"font-size:10px\">"+uF.showData(hmOrgData.get("ORG_NAME"), "")+"</span> </strong>" +
					"<div><span style=\"font-size:9px\">"+orgAdd+"</span></div></p>";
			List<Element> al = HTMLWorker.parseToList(new StringReader(orgAddress), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("Invoice", normalwithbold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			//New Row
			//first
			PdfPTable table2 = new PdfPTable(1);
			table2.setWidthPercentage(100);
			
			PdfPCell row12 = new PdfPCell(new Paragraph("Invoice To",smallBoldWhite));
			row12.setHorizontalAlignment(Element.ALIGN_CENTER);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(greenColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
//			String strClientPoc = "<p>"+uF.showData(client_poc, "")+"<div>"+client_address+"</div></p>";
//			al = HTMLWorker.parseToList(new StringReader(strClientPoc), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al);
//			row12 = new PdfPCell(new Paragraph(pr));
			String strClientPoc = uF.showData(client_poc, "")+"\n"+hmInvoiceDetails.get("CLIENT_NAME")+"\n"+uF.showData(client_address, "");
			row12 = new PdfPCell(new Paragraph(strClientPoc,small));
			row12.setHorizontalAlignment(Element.ALIGN_LEFT);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(backgroundColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
			//second
			PdfPTable table3 = new PdfPTable(1);
			table3.setWidthPercentage(100);
			
			PdfPCell row13 = new PdfPCell(new Paragraph("",small));
			row13.setHorizontalAlignment(Element.ALIGN_CENTER);
			row13.setBorder(Rectangle.NO_BORDER);
			row13.setPadding(2.5f);
			table3.addCell(row13);
			
			
			//third
			PdfPTable table4 = new PdfPTable(2);
			table4.setWidthPercentage(100);
			
			PdfPCell row14 = new PdfPCell(new Paragraph("Date",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph("Invoice No.",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			//third new row
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_CODE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_LEFT);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			
			
			PdfPTable table1 = new PdfPTable(3);
			table1.setWidthPercentage(100);
			
			PdfPCell row11 = new PdfPCell(table2);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table3);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table4);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row1 = new PdfPCell(table1);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TEMPLATE_ID")) == 1) {
				//New Row
				//first
				PdfPTable table5 = new PdfPTable(5);
				table5.setWidthPercentage(100);
				
				PdfPCell row5 = new PdfPCell(new Paragraph("Account Ref.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("P.O. No.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Terms",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Due Date",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Project Name",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				//second 
				row5 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("ACCOUNT_REF"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("PO_NO"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("TERMS"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("BILL_DUE_DATE"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PRO_NAME"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row1 = new PdfPCell(table5);
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table6 = new PdfPTable(3);
//			table6.setWidthPercentage(100);
			float[] columnWidths = new float[] {6f, 79f, 15f};
	        table6.setWidths(columnWidths);
			
			PdfPCell row6 = new PdfPCell(new Paragraph("#",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Description",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			
			//second 
			int pertiSize = 0;
			for (int i = 0,k=1; outerAmtList != null && i < outerAmtList.size(); i++) {
				List<String> alInner = outerAmtList.get(i);
				if (uF.parseToDouble(alInner.get(2)) == 0.0) {
					continue;
				}

				row6 = new PdfPCell(new Paragraph(k+". ",small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(alInner.get(1), ""), small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(alInner.get(2))), small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				pertiSize++;
				k++;
			}
			
			//second new row
			String strLines = "";
			for (int i = pertiSize; i < 27; i++) {
				strLines = strLines + "\n";
			}
			
			row6 = new PdfPCell(new Paragraph(strLines,small));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(backgroundColor);
			row6.setColspan(3);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row1 = new PdfPCell(table6);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table7 = new PdfPTable(5);
			table7.setWidthPercentage(100);
			
			PdfPCell row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Subtotal",smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//second
			row7 = new PdfPCell(new Paragraph("Other Fees / Taxes", smallBoldWhite));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(greenColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//third
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				
				row7 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), ""),small));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setColspan(3);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph("",smallBold));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
			}
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Total"+uF.showData(currency, ""),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			double totalAmt=uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"));
			String digitTotal="";
	        String strTotalAmt=""+totalAmt;
	        if(strTotalAmt.contains(".")){
	        	strTotalAmt=strTotalAmt.replace(".", ",");
	        	String[] temp=strTotalAmt.split(",");
	        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
	        	if(uF.parseToInt(temp[1])>0){
	        		int pamt=0;
	        		if(temp[1].length()==1){
	        			pamt=uF.parseToInt(temp[1]+"0");
	        		}else{
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" "+uF.showData(hmCurr.get("CURR_SUB_DIVISION"), "");
	        	}
	        }else{
	        	int totalAmt1=(int)totalAmt;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
			
			row7 = new PdfPCell(new Paragraph(""+digitTotal,small));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(5);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row1 = new PdfPCell(table7);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			if(uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")) > 0) {
				//New Row
				row1 = new PdfPCell(new Paragraph("Payment Mode:", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row       
				//New Row       
				StringBuilder sbBankAccData = new StringBuilder();
				StringBuilder sbBankAccData1 = new StringBuilder();
				StringBuilder sbBankAccData2 = new StringBuilder();
				sbBankAccData.append("Bank Details: \n");
				sbBankAccData.append("A/C No.: "+hmBankBranch.get("BRANCH_ACCOUNT_NO")+"\n");
				sbBankAccData.append("Branch: "+hmBankBranch.get("BRANCH_BRANCH")+"\n");
				sbBankAccData.append("Bank: "+hmBankBranch.get("BRANCH_BANK_NAME")+"\n");
				sbBankAccData1.append("\n");
				if(hmBankBranch.get("BRANCH_IFSC_CODE") != null && !hmBankBranch.get("BRANCH_IFSC_CODE").equals("")) {
					sbBankAccData1.append("IFSC: "+hmBankBranch.get("BRANCH_IFSC_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_SWIFT_CODE") != null && !hmBankBranch.get("BRANCH_SWIFT_CODE").equals("")) {
					sbBankAccData1.append("SWIFT: "+hmBankBranch.get("BRANCH_SWIFT_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_CLEARING_CODE") != null && !hmBankBranch.get("BRANCH_CLEARING_CODE").equals("")) {
					sbBankAccData1.append("BCC: "+hmBankBranch.get("BRANCH_CLEARING_CODE")+"\n");
				}
				sbBankAccData2.append("\n");
				if(hmBankBranch.get("OTHER_INFO") != null && !hmBankBranch.get("OTHER_INFO").equals("")) {
					sbBankAccData2.append(hmBankBranch.get("OTHER_INFO")+"\n");
				}
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData1.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData2.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
			}
			
			//New Row
			if(hmInvoiceDetails.get("PAYPAL_MAIL_ID") !=null && !hmInvoiceDetails.get("PAYPAL_MAIL_ID").trim().equals("")){
				row1 = new PdfPCell(new Paragraph("Paypal- "+uF.showData(hmInvoiceDetails.get("PAYPAL_MAIL_ID"), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
			}

			//New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setIndent(10.0f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("\n\nAuthorised Signatory,", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("This is computer generated statement and does not need signature.", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table8 = new PdfPTable(5);
			table8.setWidthPercentage(100);
			
			PdfPCell row8 = new PdfPCell(new Paragraph("Phone No.",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("E-mail",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("Web Site",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			
			
			//second 
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_CONTACT"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_EMAIL"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_WEBSITE"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row1 = new PdfPCell(table8);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			/*String strPath = request.getRealPath("/images1/icons/icons/taskrig.png");
			System.out.println("request.getRealPath()======>"+request.getRealPath("/images1/icons/icons/taskrig.png"));
			System.out.println("request.getContextPath()======>"+request.getContextPath()+"/images1/icons/icons/taskrig.png");
			String strPoweredBy = "Powered by <img src='"+strPath+"' height=\"25\">";
			List<Element> al2 = HTMLWorker.parseToList(new StringReader(strPoweredBy), null);
			Paragraph pr2 = new Paragraph("",small);
			pr2.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr2));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);*/
			
			document.add(table);
			
//			Image imageProductLogo=Image.getInstance(request.getRealPath("/images1/icons/icons/taskrig.png"));
//			imageProductLogo.setAbsolutePosition(445, 0);
//			imageProductLogo.scaleToFit(100, 100);
//			document.add(imageProductLogo);
			
			document.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	private void generateProjectPdfReportOtherCurr(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmAccNoBankName = CF.getBankAccNoMap(con, uF);
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);

			pst = con.prepareStatement(" select * from promntc_invoice_details where pro_id = ? and promntc_invoice_id=?");
			pst.setInt(1, getPro_id());
			pst.setInt(2, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			while (rs.next()) {

				hmInvoiceDetails.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoiceDetails.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmInvoiceDetails.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("REFERENCE_NO_DESC", rs.getString("reference_no_desc"));
				hmInvoiceDetails.put("PROJECT_ID", rs.getString("pro_id"));
				hmInvoiceDetails.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoiceDetails.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmInvoiceDetails.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmInvoiceDetails.put("SPOC_ID", rs.getString("spoc_id"));
				hmInvoiceDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmInvoiceDetails.put("CLIENT_NAME", uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A"));
				hmInvoiceDetails.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmInvoiceDetails.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInvoiceDetails.put("DEPART_ID", rs.getString("depart_id"));
				hmInvoiceDetails.put("INVOICE_AMOUNT", rs.getString("invoice_amount"));

				hmInvoiceDetails.put("OC_INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
				hmInvoiceDetails.put("OC_PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));
				hmInvoiceDetails.put("OC_OTHER_AMOUNT", rs.getString("oc_other_amount"));

				hmInvoiceDetails.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("particulars_total_amount"));

				hmInvoiceDetails.put("OTHER_AMOUNT", rs.getString("other_amount"));
				hmInvoiceDetails.put("OTHER_PARTICULAR", rs.getString("other_particular"));
				hmInvoiceDetails.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
				hmInvoiceDetails.put("CURR_ID", rs.getString("curr_id"));

				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));
				
				hmInvoiceDetails.put("PRO_FREQ_ID", rs.getString("pro_freq_id"));
				hmInvoiceDetails.put("INVOICE_TEMPLATE_ID", rs.getString("invoice_template_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProjectOwnerDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod "
					+ "where eod.emp_id=epd.emp_per_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_OWNER_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmProjectOwnerDetails.put("EMP_ID", rs.getString("emp_id"));
				hmProjectOwnerDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmProjectOwnerDetails.put("EMP_ORG_ID", rs.getString("org_id"));
				hmProjectOwnerDetails.put("EMP_PAN_NO", rs.getString("emp_pan_no"));
				hmProjectOwnerDetails.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				hmProjectOwnerDetails.put("EMP_WORK_LOCATION", rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'PARTI' and parent_parti_id =0 ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<List<String>> outerAmtList = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'PARTI' and parent_parti_id >0");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmChildAmt = new HashMap<String, List<Map<String, String>>>();
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_INVOICE_AMT_ID",rs.getString("promntc_invoice_amt_id"));
				hmInner.put("INVOICE_PARTICULARS",rs.getString("invoice_particulars"));
				hmInner.put("INVOICE_PARTICULARS_AMT",rs.getString("invoice_particulars_amount"));
				hmInner.put("OC_INVOICE_PARTICULARS_AMT",rs.getString("oc_invoice_particulars_amount"));
				hmInner.put("INVOIVE_ID",rs.getString("promntc_invoice_id"));
				hmInner.put("HEAD_TYPE",rs.getString("head_type"));
				
				List<Map<String, String>> innerList = new ArrayList<Map<String, String>>();
				innerList.add(hmInner);
				hmChildAmt.put(rs.getString("parent_parti_id"), innerList);
			}
			rs.close();
			pst.close(); 
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'OPE'");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'TAX' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<List<String>> outerTaxList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars_label"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				innerList.add(rs.getString("tax_percent"));
				outerTaxList.add(innerList);
			}
			rs.close();
			pst.close();
			

			pst = con.prepareStatement("select * from work_location_info where weightage > 0 order by weightage");
			rs = pst.executeQuery();
			String wLocation = "Offices at: ";
			int j = 0;
			while (rs.next()) {
				if (j == 0) {
					wLocation += rs.getString("wlocation_name");
				} else {
					wLocation += ", " + rs.getString("wlocation_name");
				}
				j++;
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMapForBilling(con);
			if (hmWorkLocation == null)
				hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmWlocation = hmWorkLocation.get(hmProjectOwnerDetails.get("EMP_WORK_LOCATION"));

			
			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd  where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
			rs = pst.executeQuery();
			String client_name = "";
			String client_id = "";
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				client_id = rs.getString("client_id");
				client_name = rs.getString("client_name");

				hmProjectDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProjectDetails.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PRIORITY", rs.getString("priority"));
				hmProjectDetails.put("DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("ACTIVITY", rs.getString("activity"));
				hmProjectDetails.put("SERVICE", rs.getString("service"));
				hmProjectDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmProjectDetails.put("DEPARTMENT_ID", rs.getString("department_id"));
				hmProjectDetails.put("PRO_CURR_ID", rs.getString("billing_curr_id"));
				
				hmProjectDetails.put("BANK_ID", rs.getString("bank_id"));
				hmProjectDetails.put("PRO_BANK_NAME", uF.showData(hmAccNoBankName.get(rs.getString("bank_id")), ""));
				hmProjectDetails.put("PAYPAL_MAIL_ID", rs.getString("paypal_mail_id"));
				hmProjectDetails.put("ACCOUNT_REF", rs.getString("acc_ref"));
				hmProjectDetails.put("PO_NO", rs.getString("po_no"));
				hmProjectDetails.put("TERMS", rs.getString("terms"));
				hmProjectDetails.put("BILL_DUE_DATE", uF.getDateFormat(rs.getString("bill_due_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");
			pst.setInt(1, getPro_id());
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData.put("CLIENT_ID", rs.getString("client_id"));
				hmProjectData.put("ORG_ID", rs.getString("org_id"));
				hmProjectData.put("WLOCATION_ID", rs.getString("wlocation_id"));
			//===start parvez date: 17-10-2022===	
//				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owner"));
				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owners"));
			//===end parvez date: 17-10-2022===	
				hmProjectData.put("PROJECT_CURRENCY", rs.getString("billing_curr_id"));
				hmProjectData.put("CLIENT_SPOC", rs.getString("poc"));
				hmProjectData.put("INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF, hmProjectData.get("ORG_ID"));
			if(hmOrgData==null) hmOrgData = new HashMap<String, String>();

			pst = con.prepareStatement("select * from client_details where client_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("CLIENT_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(", ", ",\n") : "";
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
			rs = pst.executeQuery();
			String client_poc = "";
			while (rs.next()) {
				client_poc = uF.showData(rs.getString("contact_fname"), "")+" "+ uF.showData(rs.getString("contact_mname"), "") +" "+ uF.showData(rs.getString("contact_lname"), "");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select bd1.bank_name as branch_bank_name,bd.* from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id and " +
					"bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("branch_bank_name"));
				hmBankBranch.put("BRANCH_ID", rs.getString("branch_id"));
				hmBankBranch.put("BRANCH_CODE", rs.getString("branch_code"));
				hmBankBranch.put("BRANCH_DESCRIPTION", rs.getString("bank_description"));
				hmBankBranch.put("BRANCH_ADDRESS", rs.getString("bank_address"));
				hmBankBranch.put("BRANCH_CITY", rs.getString("bank_city"));
				hmBankBranch.put("BRANCH_STATE_ID", rs.getString("bank_state_id"));
				hmBankBranch.put("BRANCH_COUNTRY_ID", rs.getString("bank_country_id"));
				hmBankBranch.put("BRANCH_BRANCH", rs.getString("bank_branch"));
				hmBankBranch.put("BRANCH_EMAIL", rs.getString("bank_email"));
				hmBankBranch.put("BRANCH_FAX", rs.getString("bank_fax"));
				hmBankBranch.put("BRANCH_CONTACT", rs.getString("bank_contact"));
				hmBankBranch.put("OTHER_INFO", rs.getString("other_information"));
				if(uF.parseToBoolean(rs.getString("is_ifsc"))) {
					hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_swift"))) {
					hmBankBranch.put("BRANCH_SWIFT_CODE", rs.getString("swift_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_clearing_code"))) {
					hmBankBranch.put("BRANCH_CLEARING_CODE", rs.getString("bank_clearing_code"));
				}
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
				hmBankBranch.put("BRANCH_SWIFT_CODE", rs.getString("swift_code"));
				hmBankBranch.put("BRANCH_BANK_CLEARING_CODE", rs.getString("bank_clearing_code"));
				hmBankBranch.put("BRANCH_OTHER_INFO", rs.getString("other_information"));
			}
			rs.close();
			pst.close();

			ByteArrayOutputStream buffer = generatePdfDocumentOtherCurr(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, hmDesignation, 
					wLocation, client_id, client_name, client_address, outerAmtList, outerTaxList, hmProjectDetails, client_poc, hmBankBranch, 
					hmCurrencyDetails, hmWlocation,hmOrgData,hmChildAmt);
			if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
				byte[] bytes = buffer.toByteArray();
				List<String> empList = new ArrayList<String>();
			//===start parvez date: 17-10-2022===	
				/*if(uF.parseToInt(hmProjectData.get("PROJECT_OWNER")) > 0){
					empList.add(hmProjectData.get("PROJECT_OWNER"));
				}*/
				
				List<String> arrPartnersIds = null;
				if(hmProjectData.get("PROJECT_OWNER") != null){
	    			arrPartnersIds = Arrays.asList(hmProjectData.get("PROJECT_OWNER").split(","));
	    		}
				
				for(int ii=1; arrPartnersIds!=null && ii<arrPartnersIds.size(); ii++){
					if(uF.parseToInt(arrPartnersIds.get(ii)) > 0){
						empList.add(arrPartnersIds.get(ii));
					}
				}
			//===end parvez date: 17-10-2022===	
				
				pst = con.prepareStatement("SELECT eod.emp_id FROM user_details ud, employee_official_details eod WHERE ud.usertype_id = 4 and ud.emp_id = eod.emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !empList.contains(rs.getString("emp_id"))){
						empList.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_FREQ_ID")));
				rs = pst.executeQuery();
				Map<String, String> hmProjectFreqData = new HashMap<String, String>();
				while(rs.next()) {
					hmProjectFreqData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
					hmProjectFreqData.put("PRO_FREQ_NAME", rs.getString("pro_freq_name"));
					hmProjectFreqData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
					hmProjectFreqData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_PAYMENT_ALERT, CF);
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);

				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
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
					nF.setStrProjectFreqName(hmProjectFreqData.get("PRO_NAME")+" ("+hmProjectFreqData.get("PRO_FREQ_NAME")+")");
					nF.setStrFromDate(hmProjectFreqData.get("PRO_FREQ_START_DATE"));
					nF.setStrToDate(hmProjectFreqData.get("PRO_FREQ_END_DATE"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
					nF.setPdfData(bytes);
					nF.setStrAttachmentFileName("Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
					nF.sendNotifications(); 
				}
				
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
					Map<String, String> hmEmpDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from employee_personal_details epd where epd.emp_per_id=? ");
					pst.setInt(1, uF.parseToInt(empList.get(i)));
					rs = pst.executeQuery();
					boolean empFlag = false;
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("emp_per_id"))> 0) {
							hmEmpDetails.put(rs.getString("emp_per_id")+"_FNAME", rs.getString("emp_fname"));
							hmEmpDetails.put(rs.getString("emp_per_id")+"_LNAME", rs.getString("emp_lname"));
							if(rs.getString("emp_email_sec") !=null && rs.getString("emp_email_sec").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email_sec"));
							} else if(rs.getString("emp_email") !=null && rs.getString("emp_email").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email"));
							}
							hmEmpDetails.put(rs.getString("emp_per_id")+"_CONTACT_NO", rs.getString("emp_contactno_mob"));
							
							empFlag = true;
						}
					}
					rs.close();
					pst.close();
					
					if(empFlag){
						nF.setStrCustFName(hmEmpDetails.get(empList.get(i)+"_FNAME"));
						nF.setStrCustLName(hmEmpDetails.get(empList.get(i)+"_LNAME"));
						nF.setStrEmpMobileNo(hmEmpDetails.get(empList.get(i)+"_CONTACT_NO"));
						nF.setStrEmpEmail(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						nF.setStrEmailTo(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrProjectFreqName(hmProjectFreqData.get("PRO_NAME")+" ("+hmProjectFreqData.get("PRO_FREQ_NAME")+")");
						nF.setStrFromDate(hmProjectFreqData.get("PRO_FREQ_START_DATE"));
						nF.setStrToDate(hmProjectFreqData.get("PRO_FREQ_END_DATE"));
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
						nF.setPdfData(bytes);
						nF.setStrAttachmentFileName("Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
						nF.sendNotifications();
					}
				}
				
			}else if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
				String directory = CF.getStrDocSaveLocation()+I_TEMP+"/"; 
				FileUtils.forceMkdir(new File(directory));
				
				byte[] bytes = buffer.toByteArray();
				File f = File.createTempFile("tmp", ".pdf", new File(directory));
				FileOutputStream fileOuputStream = new FileOutputStream(f); 
				fileOuputStream.write(bytes);
				
				String filePath = CF.getStrDocRetriveLocation()+I_TEMP+"/"+f.getName();
				request.setAttribute("filePath",filePath);
				
			} else if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private ByteArrayOutputStream generatePdfDocumentOtherCurr(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails,
			Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String wLocation, String client_id,
			String client_name, String client_address, List<List<String>> outerAmtList, List<List<String>> outerTaxList, Map<String, String> hmProjectDetails,
			String client_poc, Map<String, String> hmBankBranch, Map<String, Map<String, String>> hmCurrencyDetails, Map<String, String> hmWlocation,
			Map<String, String> hmOrgData,Map<String, List<Map<String, String>>> hmChildAmt) {
		System.out.println("generatePdfDocumentOtherCurr ======");
		
//		Font font = FontFactory.getFont("/fonts/Calibri.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		Font font = FontFactory.getFont("/fonts/arial.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		BaseFont baseFont = font.getBaseFont();
		Font heading = new Font(baseFont, 13);
		Font normal = new Font(baseFont, 11);
		Font normal1 = new Font(baseFont, 9);
		Font normalwithbold = new Font(baseFont, 14, Font.BOLD);
		Font small = new Font(baseFont, 8);
		Font small1 = new Font(baseFont, 9);
		Font smallBold = new Font(baseFont, 9, Font.BOLD);
		Font italicEffect = new Font(baseFont, 9, Font.ITALIC);
		Font smallBoldWhite = new Font(baseFont, 8, Font.BOLD, BaseColor.WHITE);
		BaseColor greenColor = WebColors.getRGBColor("#3B9C9C");
		BaseColor backgroundColor = WebColors.getRGBColor("#F0F0F0");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			
			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null) hmCurr = new HashMap<String, String>();
			String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";
			
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);
			
			//New Row
			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			String orgLogo = "";
			if(hmOrgData.get("ORG_LOGO")!=null && !hmOrgData.get("ORG_LOGO").trim().equals("")){
//				orgLogo = "<img src='"+CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO") +"' height=\"40\" width=\"160\">";
				orgLogo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO");
			}
//			List<Element> al = HTMLWorker.parseToList(new StringReader(orgLogo), null);
//			Paragraph pr = new Paragraph("",small);
//			pr.addAll(al);
//			row1 =new PdfPCell(new Paragraph(pr));
			FileInputStream fileInputStream=null;
	        File file = new File(orgLogo);
	        byte[] bFile = new byte[(int) file.length()];
	        fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    Image imageLogo = Image.getInstance(bFile);
			row1 =new PdfPCell(imageLogo,true);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);  
			
			String orgAdd = hmOrgData.get("ORG_ADDRESS") != null ? hmOrgData.get("ORG_ADDRESS").replace(", ", ",\n") + "- "
					+ uF.showData(hmOrgData.get("ORG_PINCODE"), "") : "";
			String orgAddress = "<p><strong><span style=\"font-size:10px\">"+uF.showData(hmOrgData.get("ORG_NAME"), "")+"</span> </strong>" +
					"<div><span style=\"font-size:9px\">"+orgAdd+"</span></div></p>";
			List<Element> al = HTMLWorker.parseToList(new StringReader(orgAddress), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("Invoice", normalwithbold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			//New Row
			//first
			PdfPTable table2 = new PdfPTable(1);
			table2.setWidthPercentage(100);
			
			PdfPCell row12 = new PdfPCell(new Paragraph("Invoice To",smallBoldWhite));
			row12.setHorizontalAlignment(Element.ALIGN_CENTER);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(greenColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
			/*String strClientPoc = "<p>"+uF.showData(client_poc, "")+"<div>"+client_address+"</div></p>";
			al = HTMLWorker.parseToList(new StringReader(strClientPoc), null);
			pr = new Paragraph("",small);
			pr.addAll(al);
			row12 = new PdfPCell(new Paragraph(pr));*/
			String strClientPoc = uF.showData(client_poc, "")+"\n"+hmInvoiceDetails.get("CLIENT_NAME")+"\n"+uF.showData(client_address, "");
			row12 = new PdfPCell(new Paragraph(strClientPoc,small));
			row12.setHorizontalAlignment(Element.ALIGN_LEFT);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(backgroundColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
			//second
			PdfPTable table3 = new PdfPTable(1);
			table3.setWidthPercentage(100);
			
			PdfPCell row13 = new PdfPCell(new Paragraph("",small));
			row13.setHorizontalAlignment(Element.ALIGN_CENTER);
			row13.setBorder(Rectangle.NO_BORDER);
			row13.setPadding(2.5f);
			table3.addCell(row13);
			
			
			//third
			PdfPTable table4 = new PdfPTable(2);
			table4.setWidthPercentage(100);
			
			PdfPCell row14 = new PdfPCell(new Paragraph("Date",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph("Invoice No.",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			//third new row
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_CODE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_LEFT);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			
			
			PdfPTable table1 = new PdfPTable(3);
			table1.setWidthPercentage(100);
			
			PdfPCell row11 = new PdfPCell(table2);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table3);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table4);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row1 = new PdfPCell(table1);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TEMPLATE_ID")) == 1) {
				//New Row
				//first
				PdfPTable table5 = new PdfPTable(5);
				table5.setWidthPercentage(100);
				
				PdfPCell row5 = new PdfPCell(new Paragraph("Account Ref.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("P.O. No.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Terms",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Due Date",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Project Name",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				//second 
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("ACCOUNT_REF"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PO_NO"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("TERMS"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("BILL_DUE_DATE"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PRO_NAME"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row1 = new PdfPCell(table5);
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table6 = new PdfPTable(4);
//			table6.setWidthPercentage(100);
			float[] columnWidths = new float[] {6f, 64f, 15f, 15f};
            table6.setWidths(columnWidths);
			
			PdfPCell row6 = new PdfPCell(new Paragraph("#",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Description",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			//second 
			
			int pertiSize = 0;
			
			for (int i = 0,k=1; outerAmtList != null && i < outerAmtList.size(); i++) {
				List<String> innerList = outerAmtList.get(i);
				if (innerList == null) innerList = new ArrayList<String>();
				 
				if (uF.parseToDouble(innerList.get(2)) == 0.0 && (hmChildAmt.get(innerList.get(0))==null || hmChildAmt.get(innerList.get(0)).size() ==0)) {
					continue;
				}
				row6 = new PdfPCell(new Paragraph(k+". ",small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""),small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph((((hmChildAmt.get(innerList.get(0))!=null && hmChildAmt.get(innerList.get(0)).size() >0)) ? "" : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2)))),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph((((hmChildAmt.get(innerList.get(0))!=null && hmChildAmt.get(innerList.get(0)).size() >0)) ? "" : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3)))),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				pertiSize++;
				
				List<Map<String, String>> alChildAmt = hmChildAmt.get(innerList.get(0));
				for(int j = 0, x=1; alChildAmt!=null && j < alChildAmt.size(); j++){
					//PRO_INVOICE_AMT_ID INVOICE_PARTICULARS INVOICE_PARTICULARS_AMT  OC_INVOICE_PARTICULARS_AMT INVOIVE_ID HEAD_TYPE
					Map<String, String> hmInner = alChildAmt.get(j);
					
					row6 = new PdfPCell(new Paragraph("",small));
					row6.setHorizontalAlignment(Element.ALIGN_LEFT);
					row6.setBorder(Rectangle.NO_BORDER);
					row6.setBackgroundColor(backgroundColor);
					row6.setPadding(2.5f);
					table6.addCell(row6);
					
					row6 = new PdfPCell(new Paragraph(k+"."+x+" "+uF.showData(hmInner.get("INVOICE_PARTICULARS"), ""),small));
					row6.setHorizontalAlignment(Element.ALIGN_LEFT);
					row6.setBorder(Rectangle.NO_BORDER);
					row6.setBackgroundColor(backgroundColor);
					row6.setPadding(2.5f);
					table6.addCell(row6);
					
					row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMT"))),small));
					row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row6.setBorder(Rectangle.NO_BORDER);
					row6.setBackgroundColor(backgroundColor);
					row6.setPadding(2.5f);
					table6.addCell(row6);
					
					row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("OC_INVOICE_PARTICULARS_AMT"))),small));
					row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row6.setBorder(Rectangle.NO_BORDER);
					row6.setBackgroundColor(backgroundColor);
					row6.setPadding(2.5f);
					table6.addCell(row6);
					
					pertiSize++;
					x++;
				}
				
				k++;
			}
			
			//second new row
			
			String strLines = "";
			for (int i = pertiSize; i < 27; i++) {
				strLines = strLines + "\n";
			}
			
			row6 = new PdfPCell(new Paragraph(strLines,small));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(backgroundColor);
			row6.setColspan(4);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			
			row1 = new PdfPCell(table6);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table7 = new PdfPTable(6);
			table7.setWidthPercentage(100);
			
			PdfPCell row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Subtotal",smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//second
			row7 = new PdfPCell(new Paragraph("Other Fees / Taxes",smallBoldWhite));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(greenColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//third
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				
				row7 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), ""),small));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setColspan(3);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph("",smallBold));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
			}
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Total"+uF.showData(currency, ""),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//fifth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			double totalAmt=uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"));
			String digitTotal="";
	        String strTotalAmt=""+totalAmt;
	        if(strTotalAmt.contains(".")){
	        	strTotalAmt=strTotalAmt.replace(".", ",");
	        	String[] temp=strTotalAmt.split(",");
	        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
	        	if(uF.parseToInt(temp[1])>0){
	        		int pamt=0;
	        		if(temp[1].length()==1){
	        			pamt=uF.parseToInt(temp[1]+"0");
	        		}else{
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" "+uF.showData(hmCurr.get("CURR_SUB_DIVISION"), "");
	        	}
	        }else{
	        	int totalAmt1=(int)totalAmt;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
			row7 = new PdfPCell(new Paragraph(""+digitTotal,small));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row1 = new PdfPCell(table7);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			if(uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")) > 0) {
				//New Row
				row1 = new PdfPCell(new Paragraph("Payment Mode:", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row       
				//New Row       
				StringBuilder sbBankAccData = new StringBuilder();
				StringBuilder sbBankAccData1 = new StringBuilder();
				StringBuilder sbBankAccData2 = new StringBuilder();
				sbBankAccData.append("Bank Details: \n");
				sbBankAccData.append("A/C No.: "+hmBankBranch.get("BRANCH_ACCOUNT_NO")+"\n");
				sbBankAccData.append("Branch: "+hmBankBranch.get("BRANCH_BRANCH")+"\n");
				sbBankAccData.append("Bank: "+hmBankBranch.get("BRANCH_BANK_NAME")+"\n");
				sbBankAccData1.append("\n");
				if(hmBankBranch.get("BRANCH_IFSC_CODE") != null && !hmBankBranch.get("BRANCH_IFSC_CODE").equals("")) {
					sbBankAccData1.append("IFSC: "+hmBankBranch.get("BRANCH_IFSC_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_SWIFT_CODE") != null && !hmBankBranch.get("BRANCH_SWIFT_CODE").equals("")) {
					sbBankAccData1.append("SWIFT: "+hmBankBranch.get("BRANCH_SWIFT_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_CLEARING_CODE") != null && !hmBankBranch.get("BRANCH_CLEARING_CODE").equals("")) {
					sbBankAccData1.append("BCC: "+hmBankBranch.get("BRANCH_CLEARING_CODE")+"\n");
				}
				sbBankAccData2.append("\n");
				if(hmBankBranch.get("OTHER_INFO") != null && !hmBankBranch.get("OTHER_INFO").equals("")) {
					sbBankAccData2.append(hmBankBranch.get("OTHER_INFO")+"\n");
				}
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData1.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData2.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
			}
			
			//New Row
			if(hmProjectDetails.get("PAYPAL_MAIL_ID") !=null && !hmProjectDetails.get("PAYPAL_MAIL_ID").trim().equals("")){
				row1 = new PdfPCell(new Paragraph("Paypal- "+uF.showData(hmProjectDetails.get("PAYPAL_MAIL_ID"), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f); 
				table.addCell(row1);
			}

			//New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setIndent(10.0f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("\n\nAuthorised Signatory,", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("This is computer generated statement and does not need signature.", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table8 = new PdfPTable(5);
			table8.setWidthPercentage(100);
			
			PdfPCell row8 = new PdfPCell(new Paragraph("Phone No.",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("E-mail",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("Web Site",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			
			
			//second 
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_CONTACT"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_EMAIL"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_WEBSITE"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row1 = new PdfPCell(table8);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			/*String strPath = request.getRealPath("/images1/icons/icons/taskrig.png");
			System.out.println("request.getRealPath()======>"+request.getRealPath("/images1/icons/icons/taskrig.png"));
			System.out.println("request.getContextPath()======>"+request.getContextPath()+"/images1/icons/icons/taskrig.png");
			String strPoweredBy = "Powered by <img src='"+strPath+"' height=\"25\">";
			List<Element> al2 = HTMLWorker.parseToList(new StringReader(strPoweredBy), null);
			Paragraph pr2 = new Paragraph("",small);
			pr2.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr2));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);*/
			
			document.add(table);
			
//			Image imageProductLogo=Image.getInstance(request.getRealPath("/images1/icons/icons/taskrig.png"));
//			imageProductLogo.setAbsolutePosition(445, 0);
//			imageProductLogo.scaleToFit(100, 100);
//			document.add(imageProductLogo);
			
			document.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
	private String getExchangeValue(UtilityFunctions uF, String currencyType) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String inrValue = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select short_currency,long_currency,inr_value,updated_by,update_date from currency_details where currency_id = ?");
			pst.setInt(1, uF.parseToInt(currencyType));
			rs = pst.executeQuery();
			while (rs.next()) {
				inrValue = uF.showData(rs.getString("inr_value"), "");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return inrValue;
	}

	private void generateProjectPdfReport(UtilityFunctions uF) {
		System.out.println("in generateProjectPdfReport ------------>> ");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmAccNoBankName = CF.getBankAccNoMap(con, uF);
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			// Map<String, String> hmEmpEmail = CF.getEmpEmailMap();

			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);

			pst = con.prepareStatement(" select * from promntc_invoice_details where pro_id = ? and promntc_invoice_id=?");
			pst.setInt(1, getPro_id());
			pst.setInt(2, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmInvoiceDetails.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoiceDetails.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmInvoiceDetails.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("REFERENCE_NO_DESC", rs.getString("reference_no_desc"));
				hmInvoiceDetails.put("PROJECT_ID", rs.getString("pro_id"));
				hmInvoiceDetails.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoiceDetails.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmInvoiceDetails.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmInvoiceDetails.put("SPOC_ID", rs.getString("spoc_id"));
				hmInvoiceDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmInvoiceDetails.put("CLIENT_NAME", uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A"));
				hmInvoiceDetails.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmInvoiceDetails.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInvoiceDetails.put("DEPART_ID", rs.getString("depart_id"));
				hmInvoiceDetails.put("INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
				hmInvoiceDetails.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));

				hmInvoiceDetails.put("OTHER_AMOUNT", rs.getString("oc_other_amount"));
				hmInvoiceDetails.put("OTHER_PARTICULAR", rs.getString("other_particular"));
				hmInvoiceDetails.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
				hmInvoiceDetails.put("CURR_ID", rs.getString("curr_id"));

				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));
				
				hmInvoiceDetails.put("PRO_FREQ_ID", rs.getString("pro_freq_id"));
				hmInvoiceDetails.put("INVOICE_TEMPLATE_ID", rs.getString("invoice_template_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProjectOwnerDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod "
					+ "where eod.emp_id=epd.emp_per_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_OWNER_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProjectOwnerDetails.put("EMP_ID", rs.getString("emp_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				hmProjectOwnerDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmProjectOwnerDetails.put("EMP_ORG_ID", rs.getString("org_id"));
				hmProjectOwnerDetails.put("EMP_PAN_NO", rs.getString("emp_pan_no"));
				hmProjectOwnerDetails.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				hmProjectOwnerDetails.put("EMP_WORK_LOCATION", rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'PARTI' and parent_parti_id =0 ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
//			System.out.println("pst =======>> " + pst);
			List<List<String>> outerAmtList = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'PARTI' and parent_parti_id >0");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmChildAmt = new HashMap<String, List<Map<String, String>>>();
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_INVOICE_AMT_ID",rs.getString("promntc_invoice_amt_id"));
				hmInner.put("INVOICE_PARTICULARS",rs.getString("invoice_particulars"));
				hmInner.put("INVOICE_PARTICULARS_AMT",rs.getString("invoice_particulars_amount"));
				hmInner.put("OC_INVOICE_PARTICULARS_AMT",rs.getString("oc_invoice_particulars_amount"));
				hmInner.put("INVOIVE_ID",rs.getString("promntc_invoice_id"));
				hmInner.put("HEAD_TYPE",rs.getString("head_type"));
				
				List<Map<String, String>> innerList = new ArrayList<Map<String, String>>();
				innerList.add(hmInner);
				hmChildAmt.put(rs.getString("parent_parti_id"), innerList);
			}
			rs.close();
			pst.close(); 
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'OPE'");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
//			System.out.println("pst =======>> " + pst);
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("outerAmtList ===>> " + outerAmtList);
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'TAX' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
//			Map<String, Map<String, String>> hmInvoiceAmtDetails = new LinkedHashMap<String, Map<String, String>>();
			List<List<String>> outerTaxList = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars_label"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				innerList.add(rs.getString("tax_percent"));
				outerTaxList.add(innerList);
			}
			rs.close();
			pst.close();
			
			
			// pst =
			// con.prepareStatement("select * from work_location_info where wlocation_id!=? and wlocation_id!=460 ");
			pst = con.prepareStatement("select * from work_location_info where weightage > 0 order by weightage");
			// pst.setInt(1,
			// uF.parseToInt(hmProjectOwnerDetails.get("EMP_WORK_LOCATION")));
			rs = pst.executeQuery();
			String wLocation = "Offices at: ";
			int j = 0;
			while (rs.next()) {
				if (j == 0) {
					wLocation += rs.getString("wlocation_name");
				} else {
					wLocation += ", " + rs.getString("wlocation_name");
				}
				j++;
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMapForBilling(con);
			if (hmWorkLocation == null)
				hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmWlocation = hmWorkLocation.get(hmProjectOwnerDetails.get("EMP_WORK_LOCATION"));
			
			
			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd  where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
			rs = pst.executeQuery();
			String client_name = "";
			String client_id = "";
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				client_id = rs.getString("client_id");
				client_name = rs.getString("client_name");

				hmProjectDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProjectDetails.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PRIORITY", rs.getString("priority"));
				hmProjectDetails.put("DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("ACTIVITY", rs.getString("activity"));
				hmProjectDetails.put("SERVICE", rs.getString("service"));
				hmProjectDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmProjectDetails.put("DEPARTMENT_ID", rs.getString("department_id"));
				hmProjectDetails.put("PRO_CURR_ID", rs.getString("billing_curr_id"));
				
				hmProjectDetails.put("BANK_ID", rs.getString("bank_id"));
				hmProjectDetails.put("PRO_BANK_NAME", uF.showData(hmAccNoBankName.get(rs.getString("bank_id")), ""));
				hmProjectDetails.put("PAYPAL_MAIL_ID", rs.getString("paypal_mail_id"));
				hmProjectDetails.put("ACCOUNT_REF", rs.getString("acc_ref"));
				hmProjectDetails.put("PO_NO", rs.getString("po_no"));
				hmProjectDetails.put("TERMS", rs.getString("terms"));
				hmProjectDetails.put("BILL_DUE_DATE", uF.getDateFormat(rs.getString("bill_due_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");
			pst.setInt(1, getPro_id());
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData.put("CLIENT_ID", rs.getString("client_id"));
				hmProjectData.put("ORG_ID", rs.getString("org_id"));
				hmProjectData.put("WLOCATION_ID", rs.getString("wlocation_id"));
			//===start parvez date: 17-10-2022===	
//				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owner"));
				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owners"));
			//===end parvez date: 17-10-2022===	
				hmProjectData.put("PROJECT_CURRENCY", rs.getString("billing_curr_id"));
				hmProjectData.put("CLIENT_SPOC", rs.getString("poc"));
				hmProjectData.put("INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF, hmProjectData.get("ORG_ID"));
			if(hmOrgData==null) hmOrgData = new HashMap<String, String>();

			pst = con.prepareStatement("select * from client_details where client_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("CLIENT_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(", ", ",\n") : "";
				// client_address=rs.getString("client_address");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
			rs = pst.executeQuery();
			String client_poc = "";
			while (rs.next()) {
				client_poc = uF.showData(rs.getString("contact_fname"), "")+" "+ uF.showData(rs.getString("contact_mname"), "") +" "+ uF.showData(rs.getString("contact_lname"), "");
			}
			rs.close();
			pst.close();


			pst = con.prepareStatement("select bd1.bank_name as branch_bank_name,bd.* from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id and " +
					"bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("branch_bank_name"));
				hmBankBranch.put("BRANCH_ID", rs.getString("branch_id"));
				hmBankBranch.put("BRANCH_CODE", rs.getString("branch_code"));
				hmBankBranch.put("BRANCH_DESCRIPTION", rs.getString("bank_description"));
				hmBankBranch.put("BRANCH_ADDRESS", rs.getString("bank_address"));
				hmBankBranch.put("BRANCH_CITY", rs.getString("bank_city"));
				hmBankBranch.put("BRANCH_STATE_ID", rs.getString("bank_state_id"));
				hmBankBranch.put("BRANCH_COUNTRY_ID", rs.getString("bank_country_id"));
				hmBankBranch.put("BRANCH_BRANCH", rs.getString("bank_branch"));
				hmBankBranch.put("BRANCH_EMAIL", rs.getString("bank_email"));
				hmBankBranch.put("BRANCH_FAX", rs.getString("bank_fax"));
				hmBankBranch.put("BRANCH_CONTACT", rs.getString("bank_contact"));
				hmBankBranch.put("OTHER_INFO", rs.getString("other_information"));
				if(uF.parseToBoolean(rs.getString("is_ifsc"))) {
					hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_swift"))) {
					hmBankBranch.put("BRANCH_SWIFT_CODE", rs.getString("swift_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_clearing_code"))) {
					hmBankBranch.put("BRANCH_CLEARING_CODE", rs.getString("bank_clearing_code"));
				}
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();

//			System.out.println("go to pdf ......");
			ByteArrayOutputStream buffer = generatePdfDocument(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, hmDesignation, wLocation, 
					client_id, client_name, client_address, outerAmtList, outerTaxList, hmProjectDetails, client_poc, hmBankBranch, hmCurrencyDetails,
					hmWlocation,hmOrgData,hmChildAmt);
			if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
				byte[] bytes = buffer.toByteArray();
				List<String> empList = new ArrayList<String>();
			//===start parvez date: 17-10-2022===	
				/*if(uF.parseToInt(hmProjectData.get("PROJECT_OWNER")) > 0){
					empList.add(hmProjectData.get("PROJECT_OWNER"));
				}*/
				List<String> arrPartnersIds = null;
				if(hmProjectData.get("PROJECT_OWNER") != null){
	    			arrPartnersIds = Arrays.asList(hmProjectData.get("PROJECT_OWNER").split(","));
	    		}
				
				for(int ii=1; arrPartnersIds!=null && ii<arrPartnersIds.size(); ii++){
					if(uF.parseToInt(arrPartnersIds.get(ii)) > 0){
						empList.add(arrPartnersIds.get(ii));
					}
				}
			//===end parvez date: 17-10-2022===	
				
				pst = con.prepareStatement("SELECT eod.emp_id FROM user_details ud, employee_official_details eod WHERE ud.usertype_id = 4 and ud.emp_id = eod.emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !empList.contains(rs.getString("emp_id"))){
						empList.add(rs.getString("emp_id"));
					}
				}
				
				pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_FREQ_ID")));
				rs = pst.executeQuery();
				Map<String, String> hmProjectFreqData = new HashMap<String, String>();
				while(rs.next()) {
					hmProjectFreqData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
					hmProjectFreqData.put("PRO_FREQ_NAME", rs.getString("pro_freq_name"));
					hmProjectFreqData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
					hmProjectFreqData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_PAYMENT_ALERT, CF);
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);

				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
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
					nF.setStrProjectFreqName(hmProjectFreqData.get("PRO_NAME")+" ("+hmProjectFreqData.get("PRO_FREQ_NAME")+")");
					nF.setStrFromDate(hmProjectFreqData.get("PRO_FREQ_START_DATE"));
					nF.setStrToDate(hmProjectFreqData.get("PRO_FREQ_END_DATE"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
					nF.setPdfData(bytes);
					nF.setStrAttachmentFileName("Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
					nF.sendNotifications(); 
				}
				
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
					Map<String, String> hmEmpDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from employee_personal_details epd where epd.emp_per_id=? ");
					pst.setInt(1, uF.parseToInt(empList.get(i)));
					rs = pst.executeQuery();
					boolean empFlag = false;
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("emp_per_id"))> 0) {
							hmEmpDetails.put(rs.getString("emp_per_id")+"_FNAME", rs.getString("emp_fname"));
							hmEmpDetails.put(rs.getString("emp_per_id")+"_LNAME", rs.getString("emp_lname"));
							if(rs.getString("emp_email_sec") !=null && rs.getString("emp_email_sec").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email_sec"));
							} else if(rs.getString("emp_email") !=null && rs.getString("emp_email").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email"));
							}
							hmEmpDetails.put(rs.getString("emp_per_id")+"_CONTACT_NO", rs.getString("emp_contactno_mob"));
							
							empFlag = true;
						}
					}
					rs.close();
					pst.close();
					
					if(empFlag){
						nF.setStrCustFName(hmEmpDetails.get(empList.get(i)+"_FNAME"));
						nF.setStrCustLName(hmEmpDetails.get(empList.get(i)+"_LNAME"));
						nF.setStrEmpMobileNo(hmEmpDetails.get(empList.get(i)+"_CONTACT_NO"));
						nF.setStrEmpEmail(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						nF.setStrEmailTo(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrProjectFreqName(hmProjectFreqData.get("PRO_NAME")+" ("+hmProjectFreqData.get("PRO_FREQ_NAME")+")");
						nF.setStrFromDate(hmProjectFreqData.get("PRO_FREQ_START_DATE"));
						nF.setStrToDate(hmProjectFreqData.get("PRO_FREQ_END_DATE"));
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
						nF.setPdfData(bytes);
						nF.setStrAttachmentFileName("Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
						nF.sendNotifications();
					}
				}
				
			}else if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
				String directory = CF.getStrDocSaveLocation()+I_TEMP+"/"; 
				FileUtils.forceMkdir(new File(directory));
				
				byte[] bytes = buffer.toByteArray();
				File f = File.createTempFile("tmp", ".pdf", new File(directory));
				FileOutputStream fileOuputStream = new FileOutputStream(f); 
				fileOuputStream.write(bytes);
				
				String filePath = CF.getStrDocRetriveLocation()+I_TEMP+"/"+f.getName();
				request.setAttribute("filePath",filePath);
				
			} else if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private ByteArrayOutputStream generatePdfDocument(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails, 
		Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String wLocation, 
		String client_id, String client_name, String client_address, List<List<String>> outerAmtList, List<List<String>> outerTaxList, 
		Map<String, String> hmProjectDetails, String client_poc, Map<String, String> hmBankBranch, Map<String, Map<String, String>> hmCurrencyDetails, 
		Map<String, String> hmWlocation,Map<String, String> hmOrgData,Map<String, List<Map<String, String>>> hmChildAmt) {
//		System.out.println("generatePdfDocument ======");
		
//		Font font = FontFactory.getFont("/fonts/Calibri.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		Font font = FontFactory.getFont("/fonts/arial.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		BaseFont baseFont = font.getBaseFont();
		Font heading = new Font(baseFont, 13);
		Font normal = new Font(baseFont, 11);
		Font normal1 = new Font(baseFont, 9);
		Font normalwithbold = new Font(baseFont, 14, Font.BOLD);
		Font small = new Font(baseFont, 8);
		Font small1 = new Font(baseFont, 9);
		Font smallBold = new Font(baseFont, 9, Font.BOLD);
		Font italicEffect = new Font(baseFont, 9, Font.ITALIC);
		Font smallBoldWhite = new Font(baseFont, 8, Font.BOLD, BaseColor.WHITE);
		BaseColor greenColor = WebColors.getRGBColor("#3B9C9C");
		BaseColor backgroundColor = WebColors.getRGBColor("#F0F0F0");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			
			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null) hmCurr = new HashMap<String, String>();
			String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";
			
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);
			
			//New Row
			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			String orgLogo = "";
			if(hmOrgData.get("ORG_LOGO")!=null && !hmOrgData.get("ORG_LOGO").trim().equals("")){
//				orgLogo = "<img src='"+CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO") +"' height=\"40\" width=\"160\">";
				orgLogo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO");
			}
//			List<Element> al = HTMLWorker.parseToList(new StringReader(orgLogo), null);
//			Paragraph pr = new Paragraph("",small);
//			pr.addAll(al);
//			row1 =new PdfPCell(new Paragraph(pr));
			FileInputStream fileInputStream=null;
	        File file = new File(orgLogo);
	        byte[] bFile = new byte[(int) file.length()];
	        fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    Image imageLogo = Image.getInstance(bFile);
			row1 =new PdfPCell(imageLogo,true);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);  
			
			String orgAdd = hmOrgData.get("ORG_ADDRESS") != null ? hmOrgData.get("ORG_ADDRESS").replace(", ", ",\n") + "- "
					+ uF.showData(hmOrgData.get("ORG_PINCODE"), "") : "";
			String orgAddress = "<p><strong><span style=\"font-size:10px\">"+uF.showData(hmOrgData.get("ORG_NAME"), "")+"</span> </strong>" +
					"<div><span style=\"font-size:9px\">"+orgAdd+"</span></div></p>";
			List<Element> al = HTMLWorker.parseToList(new StringReader(orgAddress), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("Invoice", normalwithbold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			//New Row
			//first
			PdfPTable table2 = new PdfPTable(1);
			table2.setWidthPercentage(100);
			
			PdfPCell row12 = new PdfPCell(new Paragraph("Invoice To",smallBoldWhite));
			row12.setHorizontalAlignment(Element.ALIGN_CENTER);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(greenColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
//			String strClientPoc = "<p>"+uF.showData(client_poc, "")+"<div>"+client_address+"</div></p>";
//			al = HTMLWorker.parseToList(new StringReader(strClientPoc), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al);
//			row12 = new PdfPCell(new Paragraph(pr));
			
			String strClientPoc = uF.showData(client_poc, "")+"\n"+hmInvoiceDetails.get("CLIENT_NAME")+"\n"+uF.showData(client_address, "");
			row12 = new PdfPCell(new Paragraph(strClientPoc,small));
			row12.setHorizontalAlignment(Element.ALIGN_LEFT);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(backgroundColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
			//second
			PdfPTable table3 = new PdfPTable(1);
			table3.setWidthPercentage(100);
			
			PdfPCell row13 = new PdfPCell(new Paragraph("",small));
			row13.setHorizontalAlignment(Element.ALIGN_CENTER);
			row13.setBorder(Rectangle.NO_BORDER);
			row13.setPadding(2.5f);
			table3.addCell(row13);
			
			
			//third
			PdfPTable table4 = new PdfPTable(2);
			table4.setWidthPercentage(100);
			
			PdfPCell row14 = new PdfPCell(new Paragraph("Date",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph("Invoice No.",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			//third new row
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_CODE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_LEFT);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			
			
			PdfPTable table1 = new PdfPTable(3);
			table1.setWidthPercentage(100);
			
			PdfPCell row11 = new PdfPCell(table2);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table3);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table4);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row1 = new PdfPCell(table1);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TEMPLATE_ID")) == 1) {
				//New Row
				//first
				PdfPTable table5 = new PdfPTable(5);
				table5.setWidthPercentage(100);
				
				PdfPCell row5 = new PdfPCell(new Paragraph("Account Ref.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("P.O. No.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Terms",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Due Date",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Project Name",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				//second 
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("ACCOUNT_REF"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PO_NO"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("TERMS"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("BILL_DUE_DATE"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PRO_NAME"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row1 = new PdfPCell(table5);
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table6 = new PdfPTable(3);
//			table6.setWidthPercentage(100);
			float[] columnWidths = new float[] {6f, 79f, 15f};
	        table6.setWidths(columnWidths);
			
			PdfPCell row6 = new PdfPCell(new Paragraph("#",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Description",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			
			//second 
			int pertiSize = 0;
			for (int i = 0,k=1; outerAmtList != null && i < outerAmtList.size(); i++) {
				List<String> alInner = outerAmtList.get(i);
				if (uF.parseToDouble(alInner.get(2)) == 0.0 && (hmChildAmt.get(alInner.get(0))==null || hmChildAmt.get(alInner.get(0)).size() ==0)) {
					continue;
				}

				row6 = new PdfPCell(new Paragraph(k+". ",small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(alInner.get(1), ""),small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(alInner.get(2), ""),small));
				row6 = new PdfPCell(new Paragraph((((hmChildAmt.get(alInner.get(0))!=null && hmChildAmt.get(alInner.get(0)).size() >0)) ? "" : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(alInner.get(2)))),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				pertiSize++;
				
				List<Map<String, String>> alChildAmt = hmChildAmt.get(alInner.get(0));
				for(int j = 0, x=1; alChildAmt!=null && j < alChildAmt.size(); j++){
					//PRO_INVOICE_AMT_ID INVOICE_PARTICULARS INVOICE_PARTICULARS_AMT  OC_INVOICE_PARTICULARS_AMT INVOIVE_ID HEAD_TYPE
					Map<String, String> hmInner = alChildAmt.get(j);
					
					row6 = new PdfPCell(new Paragraph("",small));
					row6.setHorizontalAlignment(Element.ALIGN_LEFT);
					row6.setBorder(Rectangle.NO_BORDER);
					row6.setBackgroundColor(backgroundColor);
					row6.setPadding(2.5f);
					table6.addCell(row6);
					
					row6 = new PdfPCell(new Paragraph(k+"."+x+" "+uF.showData(hmInner.get("INVOICE_PARTICULARS"), ""),small));
					row6.setHorizontalAlignment(Element.ALIGN_LEFT);
					row6.setBorder(Rectangle.NO_BORDER);
					row6.setBackgroundColor(backgroundColor);
					row6.setPadding(2.5f);
					table6.addCell(row6);
					
					row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("OC_INVOICE_PARTICULARS_AMT"))),small));
					row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row6.setBorder(Rectangle.NO_BORDER);
					row6.setBackgroundColor(backgroundColor);
					row6.setPadding(2.5f);
					table6.addCell(row6);
					
					pertiSize++;
					x++;
				}
				
				k++;
			}
			
			//second new row
			String strLines = "";
			for (int i = pertiSize; i < 27; i++) {
				strLines = strLines + "\n";
			}
			
			row6 = new PdfPCell(new Paragraph(strLines,small));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(backgroundColor);
			row6.setColspan(3);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row1 = new PdfPCell(table6);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table7 = new PdfPTable(5);
			table7.setWidthPercentage(100);
			
			PdfPCell row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Subtotal",smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//second
			row7 = new PdfPCell(new Paragraph("Other Fees / Taxes",smallBoldWhite));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(greenColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//third
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				
				row7 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), ""),small));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setColspan(3);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph("",smallBold));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
			}
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Total"+uF.showData(currency, ""),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			double totalAmt=uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"));
			String digitTotal="";
	        String strTotalAmt=""+totalAmt;
	        if(strTotalAmt.contains(".")){
	        	strTotalAmt=strTotalAmt.replace(".", ",");
	        	String[] temp=strTotalAmt.split(",");
	        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
	        	if(uF.parseToInt(temp[1])>0){
	        		int pamt=0;
	        		if(temp[1].length()==1){
	        			pamt=uF.parseToInt(temp[1]+"0");
	        		}else{
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" "+uF.showData(hmCurr.get("CURR_SUB_DIVISION"), "");
	        	}
	        }else{
	        	int totalAmt1=(int)totalAmt;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
			
			row7 = new PdfPCell(new Paragraph(""+digitTotal,small));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(5);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row1 = new PdfPCell(table7);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			if(uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")) > 0) {
				//New Row
				row1 = new PdfPCell(new Paragraph("Payment Mode:", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row       
				//New Row       
				StringBuilder sbBankAccData = new StringBuilder();
				StringBuilder sbBankAccData1 = new StringBuilder();
				StringBuilder sbBankAccData2 = new StringBuilder();
				sbBankAccData.append("Bank Details: \n");
				sbBankAccData.append("A/C No.: "+hmBankBranch.get("BRANCH_ACCOUNT_NO")+"\n");
				sbBankAccData.append("Branch: "+hmBankBranch.get("BRANCH_BRANCH")+"\n");
				sbBankAccData.append("Bank: "+hmBankBranch.get("BRANCH_BANK_NAME")+"\n");
				sbBankAccData1.append("\n");
				if(hmBankBranch.get("BRANCH_IFSC_CODE") != null && !hmBankBranch.get("BRANCH_IFSC_CODE").equals("")) {
					sbBankAccData1.append("IFSC: "+hmBankBranch.get("BRANCH_IFSC_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_SWIFT_CODE") != null && !hmBankBranch.get("BRANCH_SWIFT_CODE").equals("")) {
					sbBankAccData1.append("SWIFT: "+hmBankBranch.get("BRANCH_SWIFT_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_CLEARING_CODE") != null && !hmBankBranch.get("BRANCH_CLEARING_CODE").equals("")) {
					sbBankAccData1.append("BCC: "+hmBankBranch.get("BRANCH_CLEARING_CODE")+"\n");
				}
				sbBankAccData2.append("\n");
				if(hmBankBranch.get("OTHER_INFO") != null && !hmBankBranch.get("OTHER_INFO").equals("")) {
					sbBankAccData2.append(hmBankBranch.get("OTHER_INFO")+"\n");
				}
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData1.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData2.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
			}
			
			//New Row
			if(hmProjectDetails.get("PAYPAL_MAIL_ID") !=null && !hmProjectDetails.get("PAYPAL_MAIL_ID").trim().equals("")){
				row1 = new PdfPCell(new Paragraph("Paypal- "+uF.showData(hmProjectDetails.get("PAYPAL_MAIL_ID"), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
			}
			
			//New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setIndent(10.0f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("\n\nAuthorised Signatory,", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("This is computer generated statement and does not need signature.", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table8 = new PdfPTable(5);
			table8.setWidthPercentage(100);
			
			PdfPCell row8 = new PdfPCell(new Paragraph("Phone No.",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("E-mail",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("Web Site",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			
			
			//second 
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_CONTACT"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_EMAIL"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_WEBSITE"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row1 = new PdfPCell(table8);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			/*String strPath = request.getRealPath("/images1/icons/icons/taskrig.png");
			System.out.println("request.getRealPath()======>"+request.getRealPath("/images1/icons/icons/taskrig.png"));
			System.out.println("request.getContextPath()======>"+request.getContextPath()+"/images1/icons/icons/taskrig.png");
			String strPoweredBy = "Powered by <img src='"+strPath+"' height=\"25\">";
			List<Element> al2 = HTMLWorker.parseToList(new StringReader(strPoweredBy), null);
			Paragraph pr2 = new Paragraph("",small);
			pr2.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr2));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);*/
			
			document.add(table);
			
//			Image imageProductLogo=Image.getInstance(request.getRealPath("/images1/icons/icons/taskrig.png"));
//			imageProductLogo.setAbsolutePosition(445, 0);
//			imageProductLogo.scaleToFit(100, 100);
//			document.add(imageProductLogo);
			
			document.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	
	private void generateProjectProRetaPdfReportOtherCurr(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmAccNoBankName = CF.getBankAccNoMap(con, uF);
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			// Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);

			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);

			pst = con.prepareStatement(" select * from promntc_invoice_details where pro_id = ? and promntc_invoice_id=?");
			pst.setInt(1, getPro_id());
			pst.setInt(2, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmInvoiceDetails.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoiceDetails.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmInvoiceDetails.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("REFERENCE_NO_DESC", rs.getString("reference_no_desc"));
				hmInvoiceDetails.put("PROJECT_ID", rs.getString("pro_id"));
				hmInvoiceDetails.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoiceDetails.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmInvoiceDetails.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmInvoiceDetails.put("SPOC_ID", rs.getString("spoc_id"));
				hmInvoiceDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmInvoiceDetails.put("CLIENT_NAME", uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A"));
				hmInvoiceDetails.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmInvoiceDetails.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInvoiceDetails.put("DEPART_ID", rs.getString("depart_id"));
				hmInvoiceDetails.put("INVOICE_AMOUNT", rs.getString("invoice_amount"));
				hmInvoiceDetails.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("particulars_total_amount"));
				
				hmInvoiceDetails.put("OC_INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
				hmInvoiceDetails.put("OC_PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));

				hmInvoiceDetails.put("OTHER_AMOUNT", rs.getString("oc_other_amount"));
				hmInvoiceDetails.put("OTHER_PARTICULAR", rs.getString("other_particular"));
				hmInvoiceDetails.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
				hmInvoiceDetails.put("CURR_ID", rs.getString("curr_id"));
				hmInvoiceDetails.put("BILL_TYPE", rs.getString("bill_type"));
				hmInvoiceDetails.put("INVOICE_TYPE", rs.getString("invoice_type"));
				
				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));
				
				hmInvoiceDetails.put("PRO_FREQ_ID", rs.getString("pro_freq_id"));
				hmInvoiceDetails.put("INVOICE_TEMPLATE_ID", rs.getString("invoice_template_id"));

			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProjectOwnerDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod "
					+ "where eod.emp_id=epd.emp_per_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_OWNER_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProjectOwnerDetails.put("EMP_ID", rs.getString("emp_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmProjectOwnerDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmProjectOwnerDetails.put("EMP_ORG_ID", rs.getString("org_id"));
				hmProjectOwnerDetails.put("EMP_PAN_NO", rs.getString("emp_pan_no"));
				hmProjectOwnerDetails.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				hmProjectOwnerDetails.put("EMP_WORK_LOCATION", rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'PARTI' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<List<String>> outerAmtList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'OPE' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type is null");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<Map<String, String>> empAmtList = new ArrayList<Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
//					System.out.println("INVOICE_TYPE ====>> " + hmInvoiceDetails.get("INVOICE_TYPE"));
				if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TYPE")) == ADHOC_PRORETA_INVOICE) {
//						hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("EMP_NAME", rs.getString("resource_name"));
					hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
					hmInner.put("RATE", rs.getString("_rate"));
					hmInner.put("DAY_OR_HOUR", rs.getString("day_or_hour"));
					hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
					hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
				} else {
					hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("EMP_NAME", hmEmpName.get(rs.getString("emp_id")));
					hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
					hmInner.put("RATE", rs.getString("_rate"));
					hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
					hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
				}
				empAmtList.add(hmInner);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'TAX' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
//			Map<String, Map<String, String>> hmInvoiceAmtDetails = new LinkedHashMap<String, Map<String, String>>();
			List<List<String>> outerTaxList = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars_label"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				innerList.add(rs.getString("tax_percent"));
				outerTaxList.add(innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("empAmtList ====>> " + empAmtList);

			// pst =
			// con.prepareStatement("select * from work_location_info where wlocation_id!=? and wlocation_id!=460");
			pst = con.prepareStatement("select * from work_location_info where wlocation_id!=460");
			// pst.setInt(1,
			// uF.parseToInt(hmProjectOwnerDetails.get("EMP_WORK_LOCATION")));
			rs = pst.executeQuery();
			String wLocation = "Offices at: ";
			int j = 0;
			while (rs.next()) {
				if (j == 0) {
					wLocation += rs.getString("wlocation_name");
				} else {
					wLocation += ", " + rs.getString("wlocation_name");
				}
				j++;
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMapForBilling(con);
			if (hmWorkLocation == null)
				hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmWlocation = hmWorkLocation.get(hmProjectOwnerDetails.get("EMP_WORK_LOCATION"));

			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd  where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
			rs = pst.executeQuery();
			String client_name = "";
			String client_id = "";
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				client_id = rs.getString("client_id");
				client_name = rs.getString("client_name");

				hmProjectDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProjectDetails.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PRIORITY", rs.getString("priority"));
				hmProjectDetails.put("DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("ACTIVITY", rs.getString("activity"));
				hmProjectDetails.put("SERVICE", rs.getString("service"));
				hmProjectDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmProjectDetails.put("DEPARTMENT_ID", rs.getString("department_id"));
				hmProjectDetails.put("PRO_CURR_ID", rs.getString("billing_curr_id"));
				
				hmProjectDetails.put("BANK_ID", rs.getString("bank_id"));
				hmProjectDetails.put("PRO_BANK_NAME", uF.showData(hmAccNoBankName.get(rs.getString("bank_id")), ""));
				hmProjectDetails.put("PAYPAL_MAIL_ID", rs.getString("paypal_mail_id"));
				hmProjectDetails.put("ACCOUNT_REF", rs.getString("acc_ref"));
				hmProjectDetails.put("PO_NO", rs.getString("po_no"));
				hmProjectDetails.put("TERMS", rs.getString("terms"));
				hmProjectDetails.put("BILL_DUE_DATE", uF.getDateFormat(rs.getString("bill_due_date"), DBDATE, CF.getStrReportDateFormat()));
				hmProjectDetails.put("CALCULATION_TYPE", rs.getString("actual_calculation_type"));
			}
			rs.close();
			pst.close(); 

			pst = con.prepareStatement("select * from client_details where client_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("CLIENT_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(", ", ",\n") : "";
				// client_address=rs.getString("client_address");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
			rs = pst.executeQuery();
			String client_poc = "";
			while (rs.next()) {
				client_poc = uF.showData(rs.getString("contact_fname"), "")+" "+ uF.showData(rs.getString("contact_mname"), "") +" "+ uF.showData(rs.getString("contact_lname"), "");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select bd1.bank_name as branch_bank_name,bd.* from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id and " +
					"bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("branch_bank_name"));
				hmBankBranch.put("BRANCH_ID", rs.getString("branch_id"));
				hmBankBranch.put("BRANCH_CODE", rs.getString("branch_code"));
				hmBankBranch.put("BRANCH_DESCRIPTION", rs.getString("bank_description"));
				hmBankBranch.put("BRANCH_ADDRESS", rs.getString("bank_address"));
				hmBankBranch.put("BRANCH_CITY", rs.getString("bank_city"));
				hmBankBranch.put("BRANCH_STATE_ID", rs.getString("bank_state_id"));
				hmBankBranch.put("BRANCH_COUNTRY_ID", rs.getString("bank_country_id"));
				hmBankBranch.put("BRANCH_BRANCH", rs.getString("bank_branch"));
				hmBankBranch.put("BRANCH_EMAIL", rs.getString("bank_email"));
				hmBankBranch.put("BRANCH_FAX", rs.getString("bank_fax"));
				hmBankBranch.put("BRANCH_CONTACT", rs.getString("bank_contact"));
				hmBankBranch.put("OTHER_INFO", rs.getString("other_information"));
				if(uF.parseToBoolean(rs.getString("is_ifsc"))) {
					hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_swift"))) {
					hmBankBranch.put("BRANCH_SWIFT_CODE", rs.getString("swift_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_clearing_code"))) {
					hmBankBranch.put("BRANCH_CLEARING_CODE", rs.getString("bank_clearing_code"));
				}
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");
			pst.setInt(1, getPro_id());
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData.put("CLIENT_ID", rs.getString("client_id"));
				hmProjectData.put("ORG_ID", rs.getString("org_id"));
				hmProjectData.put("WLOCATION_ID", rs.getString("wlocation_id"));
			//===start parvez date: 17-10-2022===	
//				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owner"));
				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owners"));
			//===end parvez date: 17-10-2022===	
				hmProjectData.put("PROJECT_CURRENCY", rs.getString("billing_curr_id"));
				hmProjectData.put("CLIENT_SPOC", rs.getString("poc"));
				hmProjectData.put("INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
			}
			rs.close();
			pst.close();
			
			String strOrgId = null;
			if (uF.parseToInt(getType()) == ADHOC_PRORETA_INVOICE) {
				strOrgId = CF.getEmpOrgId(con, uF, hmInvoiceDetails.get("PRO_OWNER_ID"));
			} else {
				strOrgId = hmProjectData.get("ORG_ID");
			}
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF, strOrgId);
			if(hmOrgData==null) hmOrgData = new HashMap<String, String>();

//			InvoiceFormatwiseData invoiceFormatwiseData = new InvoiceFormatwiseData(request, session, CF, uF, con, getPro_id()+"");
//			Map<String, String> hmInvoiceFormatData = invoiceFormatwiseData.getInvoiceFormatDataPDF(getInvoice_id());
//			System.out.println("hmInvoiceFormatData ===>>>> " + hmInvoiceFormatData);
			
			ByteArrayOutputStream buffer = generateProRetaPdfDocumentOtherCurr(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, hmDesignation, 
					client_id, outerAmtList, outerTaxList, hmProjectDetails, hmCurrencyDetails, empAmtList, hmOrgData,client_poc,client_address, hmBankBranch);
			if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
				byte[] bytes = buffer.toByteArray();
				List<String> empList = new ArrayList<String>();
			//===start parvez date: 17-10-2022===	
				/*if(uF.parseToInt(hmProjectData.get("PROJECT_OWNER")) > 0){
					empList.add(hmProjectData.get("PROJECT_OWNER"));
				}*/
				
				List<String> arrPartnersIds = null;
				if(hmProjectData.get("PROJECT_OWNER") != null){
	    			arrPartnersIds = Arrays.asList(hmProjectData.get("PROJECT_OWNER").split(","));
	    		}
				
				for(int ii=1; arrPartnersIds!=null && ii<arrPartnersIds.size(); ii++){
					if(uF.parseToInt(arrPartnersIds.get(ii)) > 0){
						empList.add(arrPartnersIds.get(ii));
					}
				}
			//===end parvez date: 17-10-2022===	
				
				pst = con.prepareStatement("SELECT eod.emp_id FROM user_details ud, employee_official_details eod WHERE ud.usertype_id = 4 and ud.emp_id = eod.emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !empList.contains(rs.getString("emp_id"))){
						empList.add(rs.getString("emp_id"));
					}
				}
				
				pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_FREQ_ID")));
				rs = pst.executeQuery();
				Map<String, String> hmProjectFreqData = new HashMap<String, String>();
				while(rs.next()) {
					hmProjectFreqData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
					hmProjectFreqData.put("PRO_FREQ_NAME", rs.getString("pro_freq_name"));
					hmProjectFreqData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
					hmProjectFreqData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_PAYMENT_ALERT, CF);
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);

				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
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
					nF.setStrProjectFreqName(hmProjectFreqData.get("PRO_NAME")+" ("+hmProjectFreqData.get("PRO_FREQ_NAME")+")");
					nF.setStrFromDate(hmProjectFreqData.get("PRO_FREQ_START_DATE"));
					nF.setStrToDate(hmProjectFreqData.get("PRO_FREQ_END_DATE"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
					nF.setPdfData(bytes);
					nF.setStrAttachmentFileName("Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
					nF.sendNotifications(); 
				}
				
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
					Map<String, String> hmEmpDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from employee_personal_details epd where epd.emp_per_id=? ");
					pst.setInt(1, uF.parseToInt(empList.get(i)));
					rs = pst.executeQuery();
					boolean empFlag = false;
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("emp_per_id"))> 0) {
							hmEmpDetails.put(rs.getString("emp_per_id")+"_FNAME", rs.getString("emp_fname"));
							hmEmpDetails.put(rs.getString("emp_per_id")+"_LNAME", rs.getString("emp_lname"));
							if(rs.getString("emp_email_sec") !=null && rs.getString("emp_email_sec").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email_sec"));
							} else if(rs.getString("emp_email") !=null && rs.getString("emp_email").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email"));
							}
							hmEmpDetails.put(rs.getString("emp_per_id")+"_CONTACT_NO", rs.getString("emp_contactno_mob"));
							
							empFlag = true;
						}
					}
					rs.close();
					pst.close();
					
					if(empFlag){
						nF.setStrCustFName(hmEmpDetails.get(empList.get(i)+"_FNAME"));
						nF.setStrCustLName(hmEmpDetails.get(empList.get(i)+"_LNAME"));
						nF.setStrEmpMobileNo(hmEmpDetails.get(empList.get(i)+"_CONTACT_NO"));
						nF.setStrEmpEmail(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						nF.setStrEmailTo(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrProjectFreqName(hmProjectFreqData.get("PRO_NAME")+" ("+hmProjectFreqData.get("PRO_FREQ_NAME")+")");
						nF.setStrFromDate(hmProjectFreqData.get("PRO_FREQ_START_DATE"));
						nF.setStrToDate(hmProjectFreqData.get("PRO_FREQ_END_DATE"));
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
						nF.setPdfData(bytes);
						nF.setStrAttachmentFileName("Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
						nF.sendNotifications();
					}
				}
				
			}else if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
				String directory = CF.getStrDocSaveLocation()+I_TEMP+"/"; 
				FileUtils.forceMkdir(new File(directory));
				
				byte[] bytes = buffer.toByteArray();
				File f = File.createTempFile("tmp", ".pdf", new File(directory));
				FileOutputStream fileOuputStream = new FileOutputStream(f); 
				fileOuputStream.write(bytes);
				
				String filePath = CF.getStrDocRetriveLocation()+I_TEMP+"/"+f.getName();
				request.setAttribute("filePath",filePath);
				
			} else if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private ByteArrayOutputStream generateProRetaPdfDocumentOtherCurr(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails, 
		Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String client_id, 
		List<List<String>> outerAmtList, List<List<String>> outerTaxList, Map<String, String> hmProjectDetails, 
		Map<String, Map<String, String>> hmCurrencyDetails, List<Map<String, String>> empAmtList, Map<String, String> hmOrgData, 
		String client_poc,String client_address, Map<String, String> hmBankBranch) {
		System.out.println("generateProRetaPdfDocumentOtherCurr ======");
		
//		Font font = FontFactory.getFont("/fonts/Calibri.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		Font font = FontFactory.getFont("/fonts/arial.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		BaseFont baseFont = font.getBaseFont();
		Font heading = new Font(baseFont, 13);
		Font normal = new Font(baseFont, 11);
		Font normal1 = new Font(baseFont, 9);
		Font normalwithbold = new Font(baseFont, 14, Font.BOLD);
		Font small = new Font(baseFont, 8);
		Font small1 = new Font(baseFont, 9);
		Font smallBold = new Font(baseFont, 9, Font.BOLD);
		Font italicEffect = new Font(baseFont, 9, Font.ITALIC);
		Font smallBoldWhite = new Font(baseFont, 8, Font.BOLD, BaseColor.WHITE);
		BaseColor greenColor = WebColors.getRGBColor("#3B9C9C");
		BaseColor backgroundColor = WebColors.getRGBColor("#F0F0F0");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			
			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null) hmCurr = new HashMap<String, String>();
			String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";
			
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);
			
			//New Row
			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			String orgLogo = "";
			if(hmOrgData.get("ORG_LOGO")!=null && !hmOrgData.get("ORG_LOGO").trim().equals("")){
//				orgLogo = "<img src='"+CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO") +"' height=\"40\" width=\"160\">";
				orgLogo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO");
			}
//			List<Element> al = HTMLWorker.parseToList(new StringReader(orgLogo), null);
//			Paragraph pr = new Paragraph("",small);
//			pr.addAll(al);
//			row1 =new PdfPCell(new Paragraph(pr));
			FileInputStream fileInputStream=null;
	        File file = new File(orgLogo);
	        byte[] bFile = new byte[(int) file.length()];
	        fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    Image imageLogo = Image.getInstance(bFile);
			row1 =new PdfPCell(imageLogo,true);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);  
			
			String orgAdd = hmOrgData.get("ORG_ADDRESS") != null ? hmOrgData.get("ORG_ADDRESS").replace(", ", ",\n") + "- "
					+ uF.showData(hmOrgData.get("ORG_PINCODE"), "") : "";
			String orgAddress = "<p><strong><span style=\"font-size:10px\">"+uF.showData(hmOrgData.get("ORG_NAME"), "")+"</span> </strong>" +
					"<div><span style=\"font-size:9px\">"+orgAdd+"</span></div></p>";
			List<Element> al = HTMLWorker.parseToList(new StringReader(orgAddress), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("Invoice", normalwithbold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			//New Row
			//first
			PdfPTable table2 = new PdfPTable(1);
			table2.setWidthPercentage(100);
			
			PdfPCell row12 = new PdfPCell(new Paragraph("Invoice To",smallBoldWhite));
			row12.setHorizontalAlignment(Element.ALIGN_CENTER);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(greenColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
//			String strClientPoc = "<p>"+uF.showData(client_poc, "")+"<div>"+client_address+"</div></p>";
//			al = HTMLWorker.parseToList(new StringReader(strClientPoc), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al);
//			row12 = new PdfPCell(new Paragraph(pr));
			
			String strClientPoc = uF.showData(client_poc, "")+"\n"+hmInvoiceDetails.get("CLIENT_NAME")+"\n"+uF.showData(client_address, "");
			row12 = new PdfPCell(new Paragraph(strClientPoc,small));
			row12.setHorizontalAlignment(Element.ALIGN_LEFT);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(backgroundColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
			//second
			PdfPTable table3 = new PdfPTable(1);
			table3.setWidthPercentage(100);
			
			PdfPCell row13 = new PdfPCell(new Paragraph("",small));
			row13.setHorizontalAlignment(Element.ALIGN_CENTER);
			row13.setBorder(Rectangle.NO_BORDER);
			row13.setPadding(2.5f);
			table3.addCell(row13);
			
			
			//third
			PdfPTable table4 = new PdfPTable(2);
			table4.setWidthPercentage(100);
			
			PdfPCell row14 = new PdfPCell(new Paragraph("Date",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph("Invoice No.",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			//third new row
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_CODE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_LEFT);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			
			
			PdfPTable table1 = new PdfPTable(3);
			table1.setWidthPercentage(100);
			
			PdfPCell row11 = new PdfPCell(table2);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table3);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table4);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row1 = new PdfPCell(table1);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TEMPLATE_ID")) == 1) {
				//New Row
				//first
				PdfPTable table5 = new PdfPTable(5);
				table5.setWidthPercentage(100);
				
				PdfPCell row5 = new PdfPCell(new Paragraph("Account Ref.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("P.O. No.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Terms",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Due Date",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Project Name",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				//second 
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("ACCOUNT_REF"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PO_NO"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("TERMS"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("BILL_DUE_DATE"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PRO_NAME"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row1 = new PdfPCell(table5);
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table6 = new PdfPTable(6);
//			table6.setWidthPercentage(100);
			float[] columnWidths = new float[] {6f, 34f, 15f, 15f, 15f, 15f};
            table6.setWidths(columnWidths);
			
			PdfPCell row6 = new PdfPCell(new Paragraph("#",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Description",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			String strCalType = "";
			if(hmProjectDetails.get("CALCULATION_TYPE") != null && hmProjectDetails.get("CALCULATION_TYPE").trim().equalsIgnoreCase("H")){
				strCalType = "Hours";
			} else if(hmProjectDetails.get("CALCULATION_TYPE") != null && hmProjectDetails.get("CALCULATION_TYPE").trim().equalsIgnoreCase("D")){
				strCalType = "Days";
			} else if(hmProjectDetails.get("CALCULATION_TYPE") != null && hmProjectDetails.get("CALCULATION_TYPE").trim().equalsIgnoreCase("M")){
				strCalType = "Months";
			}
			
			row6 = new PdfPCell(new Paragraph("Qty/"+strCalType,smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Rate"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			//second 
			
			int pertiSize = 0;
			
			for (int i = 0,k=1; empAmtList != null && i < empAmtList.size(); i++,k++) {
				Map<String, String> hmInner = empAmtList.get(i);
				if (hmInner == null) hmInner = new HashMap<String, String>();

//				if (uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMOUNT")) == 0.0) {
//					continue;
//				}
			
				row6 = new PdfPCell(new Paragraph(k+". ",small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				String empName = uF.showData(hmInner.get("EMP_NAME"), "");
				row6 = new PdfPCell(new Paragraph(empName,small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(hmInner.get("DAYS_HOURS"), ""),small));
				row6.setHorizontalAlignment(Element.ALIGN_CENTER);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(hmInner.get("RATE"), ""),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMOUNT"))),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("OC_INVOICE_PARTICULARS_AMOUNT"))),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				pertiSize++;
			}
			
			//second new row
			
			String strLines = "";
			for (int i = pertiSize; i < 27; i++) {
				strLines = strLines + "\n";
			}
			
			row6 = new PdfPCell(new Paragraph(strLines,small));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(backgroundColor);
			row6.setColspan(6);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			
			row1 = new PdfPCell(table6);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table7 = new PdfPTable(6);
			table7.setWidthPercentage(100);
			
			PdfPCell row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Subtotal",smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//second
			row7 = new PdfPCell(new Paragraph("Other Fees / Taxes",smallBoldWhite));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(greenColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//third
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				
				row7 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), ""),small));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setColspan(3);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph("",smallBold));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
			}
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Total"+uF.showData(currency, ""),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//fifth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			double totalAmt=uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"));
			String digitTotal="";
	        String strTotalAmt=""+totalAmt;
	        if(strTotalAmt.contains(".")){
	        	strTotalAmt=strTotalAmt.replace(".", ",");
	        	String[] temp=strTotalAmt.split(",");
	        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
	        	if(uF.parseToInt(temp[1])>0){
	        		int pamt=0;
	        		if(temp[1].length()==1){
	        			pamt=uF.parseToInt(temp[1]+"0");
	        		}else{
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" "+uF.showData(hmCurr.get("CURR_SUB_DIVISION"), "");
	        	}
	        }else{
	        	int totalAmt1=(int)totalAmt;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
			row7 = new PdfPCell(new Paragraph(""+digitTotal,small));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row1 = new PdfPCell(table7);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			if(uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")) > 0) {
				//New Row
				row1 = new PdfPCell(new Paragraph("Payment Mode:", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row       
				//New Row       
				StringBuilder sbBankAccData = new StringBuilder();
				StringBuilder sbBankAccData1 = new StringBuilder();
				StringBuilder sbBankAccData2 = new StringBuilder();
				sbBankAccData.append("Bank Details: \n");
				sbBankAccData.append("A/C No.: "+hmBankBranch.get("BRANCH_ACCOUNT_NO")+"\n");
				sbBankAccData.append("Branch: "+hmBankBranch.get("BRANCH_BRANCH")+"\n");
				sbBankAccData.append("Bank: "+hmBankBranch.get("BRANCH_BANK_NAME")+"\n");
				sbBankAccData1.append("\n");
				if(hmBankBranch.get("BRANCH_IFSC_CODE") != null && !hmBankBranch.get("BRANCH_IFSC_CODE").equals("")) {
					sbBankAccData1.append("IFSC: "+hmBankBranch.get("BRANCH_IFSC_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_SWIFT_CODE") != null && !hmBankBranch.get("BRANCH_SWIFT_CODE").equals("")) {
					sbBankAccData1.append("SWIFT: "+hmBankBranch.get("BRANCH_SWIFT_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_CLEARING_CODE") != null && !hmBankBranch.get("BRANCH_CLEARING_CODE").equals("")) {
					sbBankAccData1.append("BCC: "+hmBankBranch.get("BRANCH_CLEARING_CODE")+"\n");
				}
				sbBankAccData2.append("\n");
				if(hmBankBranch.get("OTHER_INFO") != null && !hmBankBranch.get("OTHER_INFO").equals("")) {
					sbBankAccData2.append(hmBankBranch.get("OTHER_INFO")+"\n");
				}
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData1.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData2.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
			}
			
			//New Row
			if(hmProjectDetails.get("PAYPAL_MAIL_ID") !=null && !hmProjectDetails.get("PAYPAL_MAIL_ID").trim().equals("")){
				row1 = new PdfPCell(new Paragraph("Paypal- "+uF.showData(hmProjectDetails.get("PAYPAL_MAIL_ID"), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f); 
				table.addCell(row1);
			}
			
			//New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setIndent(10.0f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("\n\nAuthorised Signatory,", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("This is computer generated statement and does not need signature.", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table8 = new PdfPTable(5);
			table8.setWidthPercentage(100);
			
			PdfPCell row8 = new PdfPCell(new Paragraph("Phone No.",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("E-mail",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("Web Site",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			
			
			//second 
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_CONTACT"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_EMAIL"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_WEBSITE"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row1 = new PdfPCell(table8);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			/*String strPath = request.getRealPath("/images1/icons/icons/taskrig.png");
			System.out.println("request.getRealPath()======>"+request.getRealPath("/images1/icons/icons/taskrig.png"));
			System.out.println("request.getContextPath()======>"+request.getContextPath()+"/images1/icons/icons/taskrig.png");
			String strPoweredBy = "Powered by <img src='"+strPath+"' height=\"25\">";
			List<Element> al2 = HTMLWorker.parseToList(new StringReader(strPoweredBy), null);
			Paragraph pr2 = new Paragraph("",small);
			pr2.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr2));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);*/
			
			document.add(table);
			
//			Image imageProductLogo=Image.getInstance(request.getRealPath("/images1/icons/icons/taskrig.png"));
//			imageProductLogo.setAbsolutePosition(445, 0);
//			imageProductLogo.scaleToFit(100, 100);
//			document.add(imageProductLogo);
			
			document.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	
	
	private void generateProjectProRetaPdfReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmAccNoBankName = CF.getBankAccNoMap(con, uF);
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			// Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);

			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);

			pst = con.prepareStatement(" select * from promntc_invoice_details where pro_id = ? and promntc_invoice_id=?");
			pst.setInt(1, getPro_id());
			pst.setInt(2, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmInvoiceDetails.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoiceDetails.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmInvoiceDetails.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("REFERENCE_NO_DESC", rs.getString("reference_no_desc"));
				hmInvoiceDetails.put("PROJECT_ID", rs.getString("pro_id"));
				hmInvoiceDetails.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoiceDetails.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmInvoiceDetails.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmInvoiceDetails.put("SPOC_ID", rs.getString("spoc_id"));
				hmInvoiceDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmInvoiceDetails.put("CLIENT_NAME", uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A"));
				hmInvoiceDetails.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmInvoiceDetails.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInvoiceDetails.put("DEPART_ID", rs.getString("depart_id"));
				hmInvoiceDetails.put("INVOICE_AMOUNT", rs.getString("invoice_amount"));
				hmInvoiceDetails.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("particulars_total_amount"));
				
				hmInvoiceDetails.put("OC_INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
				hmInvoiceDetails.put("OC_PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));
				
				hmInvoiceDetails.put("OTHER_AMOUNT", rs.getString("oc_other_amount"));
				hmInvoiceDetails.put("OTHER_PARTICULAR", rs.getString("other_particular"));
				hmInvoiceDetails.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
				hmInvoiceDetails.put("CURR_ID", rs.getString("curr_id"));
				hmInvoiceDetails.put("BILL_TYPE", rs.getString("bill_type"));
				hmInvoiceDetails.put("INVOICE_TYPE", rs.getString("invoice_type"));
				
				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));
				
				hmInvoiceDetails.put("PRO_FREQ_ID", rs.getString("pro_freq_id"));
				hmInvoiceDetails.put("INVOICE_TEMPLATE_ID", rs.getString("invoice_template_id"));

			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProjectOwnerDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod "
					+ "where eod.emp_id=epd.emp_per_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_OWNER_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProjectOwnerDetails.put("EMP_ID", rs.getString("emp_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmProjectOwnerDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmProjectOwnerDetails.put("EMP_ORG_ID", rs.getString("org_id"));
				hmProjectOwnerDetails.put("EMP_PAN_NO", rs.getString("emp_pan_no"));
				hmProjectOwnerDetails.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				hmProjectOwnerDetails.put("EMP_WORK_LOCATION", rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'PARTI' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<List<String>> outerAmtList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'OPE' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				outerAmtList.add(innerList);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type is null");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<Map<String, String>> empAmtList = new ArrayList<Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
//					System.out.println("INVOICE_TYPE ====>> " + hmInvoiceDetails.get("INVOICE_TYPE"));
				if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TYPE")) == ADHOC_PRORETA_INVOICE) {
//						hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("EMP_NAME", rs.getString("resource_name"));
					hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
					hmInner.put("RATE", rs.getString("_rate"));
					hmInner.put("DAY_OR_HOUR", rs.getString("day_or_hour"));
					hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
					hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
				} else {
					hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("EMP_NAME", hmEmpName.get(rs.getString("emp_id")));
					hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
					hmInner.put("RATE", rs.getString("_rate"));
					hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
					hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
				}
				empAmtList.add(hmInner); 
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type = 'TAX' ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
//			Map<String, Map<String, String>> hmInvoiceAmtDetails = new LinkedHashMap<String, Map<String, String>>();
			List<List<String>> outerTaxList = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars_label"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));
				innerList.add(rs.getString("head_type"));
				innerList.add(rs.getString("tax_percent"));
				outerTaxList.add(innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("empAmtList ====>> " + empAmtList);

			// pst =
			// con.prepareStatement("select * from work_location_info where wlocation_id!=? and wlocation_id!=460");
			pst = con.prepareStatement("select * from work_location_info where wlocation_id!=460");
			// pst.setInt(1,
			// uF.parseToInt(hmProjectOwnerDetails.get("EMP_WORK_LOCATION")));
			rs = pst.executeQuery();
			String wLocation = "Offices at: ";
			int j = 0;
			while (rs.next()) {
				if (j == 0) {
					wLocation += rs.getString("wlocation_name");
				} else {
					wLocation += ", " + rs.getString("wlocation_name");
				}
				j++;
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMapForBilling(con);
			if (hmWorkLocation == null)
				hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmWlocation = hmWorkLocation.get(hmProjectOwnerDetails.get("EMP_WORK_LOCATION"));

			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd  where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
			rs = pst.executeQuery();
//			String client_name = "";
//			String client_id = "";
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
//				client_id = rs.getString("client_id");
//				client_name = rs.getString("client_name");

				hmProjectDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProjectDetails.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PRIORITY", rs.getString("priority"));
				hmProjectDetails.put("DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("ACTIVITY", rs.getString("activity"));
				hmProjectDetails.put("SERVICE", rs.getString("service"));
				hmProjectDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmProjectDetails.put("DEPARTMENT_ID", rs.getString("department_id"));
				hmProjectDetails.put("PRO_CURR_ID", rs.getString("billing_curr_id"));
				
				hmProjectDetails.put("BANK_ID", rs.getString("bank_id"));
				hmProjectDetails.put("PRO_BANK_NAME", uF.showData(hmAccNoBankName.get(rs.getString("bank_id")), ""));
				hmProjectDetails.put("PAYPAL_MAIL_ID", rs.getString("paypal_mail_id"));
				hmProjectDetails.put("ACCOUNT_REF", rs.getString("acc_ref"));
				hmProjectDetails.put("PO_NO", rs.getString("po_no"));
				hmProjectDetails.put("TERMS", rs.getString("terms"));
				hmProjectDetails.put("BILL_DUE_DATE", uF.getDateFormat(rs.getString("bill_due_date"), DBDATE, CF.getStrReportDateFormat()));
				hmProjectDetails.put("CALCULATION_TYPE", rs.getString("actual_calculation_type"));
			}
			rs.close();
			pst.close(); 

			pst = con.prepareStatement("select * from client_details where client_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("CLIENT_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(", ", ",\n") : "";
				// client_address=rs.getString("client_address");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from client_poc where poc_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
			rs = pst.executeQuery();
			String client_poc = "";
			while (rs.next()) {
				client_poc = uF.showData(rs.getString("contact_fname"), "")+" "+ uF.showData(rs.getString("contact_mname"), "") +" "+ uF.showData(rs.getString("contact_lname"), "");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select bd1.bank_name as branch_bank_name,bd.* from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id and " +
					"bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("branch_bank_name"));
				hmBankBranch.put("BRANCH_ID", rs.getString("branch_id"));
				hmBankBranch.put("BRANCH_CODE", rs.getString("branch_code"));
				hmBankBranch.put("BRANCH_DESCRIPTION", rs.getString("bank_description"));
				hmBankBranch.put("BRANCH_ADDRESS", rs.getString("bank_address"));
				hmBankBranch.put("BRANCH_CITY", rs.getString("bank_city"));
				hmBankBranch.put("BRANCH_STATE_ID", rs.getString("bank_state_id"));
				hmBankBranch.put("BRANCH_COUNTRY_ID", rs.getString("bank_country_id"));
				hmBankBranch.put("BRANCH_BRANCH", rs.getString("bank_branch"));
				hmBankBranch.put("BRANCH_EMAIL", rs.getString("bank_email"));
				hmBankBranch.put("BRANCH_FAX", rs.getString("bank_fax"));
				hmBankBranch.put("BRANCH_CONTACT", rs.getString("bank_contact"));
				hmBankBranch.put("OTHER_INFO", rs.getString("other_information"));
				if(uF.parseToBoolean(rs.getString("is_ifsc"))) {
					hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_swift"))) {
					hmBankBranch.put("BRANCH_SWIFT_CODE", rs.getString("swift_code"));
				}
				if(uF.parseToBoolean(rs.getString("is_clearing_code"))) {
					hmBankBranch.put("BRANCH_CLEARING_CODE", rs.getString("bank_clearing_code"));
				}
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");
			pst.setInt(1, getPro_id());
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData.put("CLIENT_ID", rs.getString("client_id"));
				hmProjectData.put("ORG_ID", rs.getString("org_id"));
				hmProjectData.put("WLOCATION_ID", rs.getString("wlocation_id"));
			//===start parvez date: 17-10-2022===	
//				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owner"));
				hmProjectData.put("PROJECT_OWNER", rs.getString("project_owners"));
			//===end parvez date: 17-10-2022===	
				hmProjectData.put("PROJECT_CURRENCY", rs.getString("billing_curr_id"));
				hmProjectData.put("CLIENT_SPOC", rs.getString("poc"));
				hmProjectData.put("INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
			}
			rs.close();
			pst.close();
			
			String strOrgId = null;
			if (uF.parseToInt(getType()) == ADHOC_PRORETA_INVOICE) {
				strOrgId = CF.getEmpOrgId(con, uF, hmInvoiceDetails.get("PRO_OWNER_ID"));
			} else {
				strOrgId = hmProjectData.get("ORG_ID");
			}
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF, strOrgId);
			if(hmOrgData==null) hmOrgData = new HashMap<String, String>();

//			InvoiceFormatwiseData invoiceFormatwiseData = new InvoiceFormatwiseData(request, session, CF, uF, con, getPro_id()+"");
//			Map<String, String> hmInvoiceFormatData = invoiceFormatwiseData.getInvoiceFormatDataPDF(getInvoice_id());
//			System.out.println("hmInvoiceFormatData ===>>>> " + hmInvoiceFormatData);
			
			ByteArrayOutputStream buffer = generateProRetaPdfDocument(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, hmDesignation, 
					outerAmtList, outerTaxList, hmProjectDetails, hmCurrencyDetails, empAmtList, hmOrgData,client_poc,client_address, hmBankBranch);
			
			if(getOperation()!=null && getOperation().equalsIgnoreCase("mail")){
				byte[] bytes = buffer.toByteArray();
				List<String> empList = new ArrayList<String>();
			//===start parvez date: 17-10-2022===	
				/*if(uF.parseToInt(hmProjectData.get("PROJECT_OWNER")) > 0){
					empList.add(hmProjectData.get("PROJECT_OWNER"));
				}*/
				List<String> arrPartnersIds = null;
				if(hmProjectData.get("PROJECT_OWNER") != null){
	    			arrPartnersIds = Arrays.asList(hmProjectData.get("PROJECT_OWNER").split(","));
	    		}
				
				for(int ii=1; arrPartnersIds!=null && ii<arrPartnersIds.size(); ii++){
					if(uF.parseToInt(arrPartnersIds.get(ii)) > 0){
						empList.add(arrPartnersIds.get(ii));
					}
				}
			//===end parvez date: 17-10-2022===	
				
				pst = con.prepareStatement("SELECT eod.emp_id FROM user_details ud, employee_official_details eod WHERE ud.usertype_id = 4 and ud.emp_id = eod.emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !empList.contains(rs.getString("emp_id"))){
						empList.add(rs.getString("emp_id"));
					}
				}
				
				pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PRO_FREQ_ID")));
				rs = pst.executeQuery();
				Map<String, String> hmProjectFreqData = new HashMap<String, String>();
				while(rs.next()) {
					hmProjectFreqData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
					hmProjectFreqData.put("PRO_FREQ_NAME", rs.getString("pro_freq_name"));
					hmProjectFreqData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
					hmProjectFreqData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_PAYMENT_ALERT, CF);
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);

				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("SPOC_ID")));
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
					nF.setStrProjectFreqName(hmProjectFreqData.get("PRO_NAME")+" ("+hmProjectFreqData.get("PRO_FREQ_NAME")+")");
					nF.setStrFromDate(hmProjectFreqData.get("PRO_FREQ_START_DATE"));
					nF.setStrToDate(hmProjectFreqData.get("PRO_FREQ_END_DATE"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));

					nF.setPdfData(bytes);
					nF.setStrAttachmentFileName("Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
					nF.sendNotifications(); 
				}
				
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
					Map<String, String> hmEmpDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from employee_personal_details epd where epd.emp_per_id=? ");
					pst.setInt(1, uF.parseToInt(empList.get(i)));
					rs = pst.executeQuery();
					boolean empFlag = false;
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("emp_per_id"))> 0) {
							hmEmpDetails.put(rs.getString("emp_per_id")+"_FNAME", rs.getString("emp_fname"));
							hmEmpDetails.put(rs.getString("emp_per_id")+"_LNAME", rs.getString("emp_lname"));
							if(rs.getString("emp_email_sec") !=null && rs.getString("emp_email_sec").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email_sec"));
							} else if(rs.getString("emp_email") !=null && rs.getString("emp_email").indexOf("@")>0) {
								hmEmpDetails.put(rs.getString("emp_per_id")+"_EMAIL", rs.getString("emp_email"));
							}
							hmEmpDetails.put(rs.getString("emp_per_id")+"_CONTACT_NO", rs.getString("emp_contactno_mob"));
							
							empFlag = true;
						}
					}
					rs.close();
					pst.close();
					
					if(empFlag){
						nF.setStrCustFName(hmEmpDetails.get(empList.get(i)+"_FNAME"));
						nF.setStrCustLName(hmEmpDetails.get(empList.get(i)+"_LNAME"));
						nF.setStrEmpMobileNo(hmEmpDetails.get(empList.get(i)+"_CONTACT_NO"));
						nF.setStrEmpEmail(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						nF.setStrEmailTo(hmEmpDetails.get(empList.get(i)+"_EMAIL"));
						
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrProjectFreqName(hmProjectFreqData.get("PRO_NAME")+" ("+hmProjectFreqData.get("PRO_FREQ_NAME")+")");
						nF.setStrFromDate(hmProjectFreqData.get("PRO_FREQ_START_DATE"));
						nF.setStrToDate(hmProjectFreqData.get("PRO_FREQ_END_DATE"));
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						nF.setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
						nF.setPdfData(bytes);
						nF.setStrAttachmentFileName("Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
						nF.sendNotifications();
					}
				}
				
			}else if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
				String directory = CF.getStrDocSaveLocation()+I_TEMP+"/"; 
				FileUtils.forceMkdir(new File(directory));
				
				byte[] bytes = buffer.toByteArray();
				File f = File.createTempFile("tmp", ".pdf", new File(directory));
				FileOutputStream fileOuputStream = new FileOutputStream(f); 
				fileOuputStream.write(bytes);
				
				String filePath = CF.getStrDocRetriveLocation()+I_TEMP+"/"+f.getName();
				request.setAttribute("filePath",filePath);
				
			} else if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private ByteArrayOutputStream generateProRetaPdfDocument(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails, 
		Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, 
		List<List<String>> outerAmtList, List<List<String>> outerTaxList, Map<String, String> hmProjectDetails, 
		Map<String, Map<String, String>> hmCurrencyDetails, List<Map<String, String>> empAmtList, Map<String, String> hmOrgData,
		String client_poc,String client_address, Map<String, String> hmBankBranch) {
		System.out.println("generateProRetaPdfDocument ======");
		
//		Font font = FontFactory.getFont("/fonts/Calibri.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		Font font = FontFactory.getFont("/fonts/arial.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK);
		BaseFont baseFont = font.getBaseFont();
		Font heading = new Font(baseFont, 13);
		Font normal = new Font(baseFont, 11);
		Font normal1 = new Font(baseFont, 9);
		Font normalwithbold = new Font(baseFont, 14, Font.BOLD);
		Font small = new Font(baseFont, 8);
		Font small1 = new Font(baseFont, 9);
		Font smallBold = new Font(baseFont, 9, Font.BOLD);
		Font italicEffect = new Font(baseFont, 9, Font.ITALIC);
		Font smallBoldWhite = new Font(baseFont, 8, Font.BOLD, BaseColor.WHITE);
		BaseColor greenColor = WebColors.getRGBColor("#3B9C9C");
		BaseColor backgroundColor = WebColors.getRGBColor("#F0F0F0");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			
			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null) hmCurr = new HashMap<String, String>();
			String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").equals("") ? " ("+hmCurr.get("SHORT_CURR")+")" : "";
			
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);
			
			//New Row
			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			String orgLogo = "";
			if(hmOrgData.get("ORG_LOGO")!=null && !hmOrgData.get("ORG_LOGO").trim().equals("")){
//				orgLogo = "<img src='"+CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO") +"' height=\"40\" width=\"160\">";
				orgLogo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+hmOrgData.get("ORG_LOGO");
			}
//			List<Element> al = HTMLWorker.parseToList(new StringReader(orgLogo), null);
//			Paragraph pr = new Paragraph("",small);
//			pr.addAll(al);
//			row1 =new PdfPCell(new Paragraph(pr));
			FileInputStream fileInputStream=null;
	        File file = new File(orgLogo);
	        byte[] bFile = new byte[(int) file.length()];
	        fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    Image imageLogo = Image.getInstance(bFile);
			row1 =new PdfPCell(imageLogo,true);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);  
			
			String orgAdd = hmOrgData.get("ORG_ADDRESS") != null ? hmOrgData.get("ORG_ADDRESS").replace(", ", ",\n") + "- "
					+ uF.showData(hmOrgData.get("ORG_PINCODE"), "") : "";
			String orgAddress = "<p><strong><span style=\"font-size:10px\">"+uF.showData(hmOrgData.get("ORG_NAME"), "")+"</span> </strong>" +
					"<div><span style=\"font-size:9px\">"+orgAdd+"</span></div></p>";
			List<Element> al = HTMLWorker.parseToList(new StringReader(orgAddress), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("Invoice", normalwithbold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			//New Row
			//first
			PdfPTable table2 = new PdfPTable(1);
			table2.setWidthPercentage(100);
			
			PdfPCell row12 = new PdfPCell(new Paragraph("Invoice To",smallBoldWhite));
			row12.setHorizontalAlignment(Element.ALIGN_CENTER);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(greenColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
//			String strClientPoc = "<p>"+uF.showData(client_poc, "")+"<div>"+client_address+"</div></p>";
//			al = HTMLWorker.parseToList(new StringReader(strClientPoc), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al);
//			row12 = new PdfPCell(new Paragraph(pr));
			String strClientPoc = uF.showData(client_poc, "")+"\n"+hmInvoiceDetails.get("CLIENT_NAME")+"\n"+uF.showData(client_address, "");
			row12 = new PdfPCell(new Paragraph(strClientPoc,small));
			row12.setHorizontalAlignment(Element.ALIGN_LEFT);
			row12.setBorder(Rectangle.NO_BORDER);
			row12.setBackgroundColor(backgroundColor);
			row12.setPadding(2.5f);
			table2.addCell(row12);
			
			//second
			PdfPTable table3 = new PdfPTable(1);
			table3.setWidthPercentage(100);
			
			PdfPCell row13 = new PdfPCell(new Paragraph("",small));
			row13.setHorizontalAlignment(Element.ALIGN_CENTER);
			row13.setBorder(Rectangle.NO_BORDER);
			row13.setPadding(2.5f);
			table3.addCell(row13);
			
			
			//third
			PdfPTable table4 = new PdfPTable(2);
			table4.setWidthPercentage(100);
			
			PdfPCell row14 = new PdfPCell(new Paragraph("Date",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph("Invoice No.",smallBoldWhite));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(greenColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			//third new row
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_CENTER);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			row14 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("INVOICE_CODE"), ""),small));
			row14.setHorizontalAlignment(Element.ALIGN_LEFT);
			row14.setBorder(Rectangle.NO_BORDER);
			row14.setBackgroundColor(backgroundColor);
			row14.setPadding(2.5f);
			table4.addCell(row14);
			
			
			
			PdfPTable table1 = new PdfPTable(3);
			table1.setWidthPercentage(100);
			
			PdfPCell row11 = new PdfPCell(table2);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table3);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row11 = new PdfPCell(table4);
			row11.setHorizontalAlignment(Element.ALIGN_LEFT);
			row11.setBorder(Rectangle.NO_BORDER);
			row11.setPadding(2.5f);
			table1.addCell(row11);
			
			row1 = new PdfPCell(table1);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TEMPLATE_ID")) == 1) {
				//New Row
				//first
				PdfPTable table5 = new PdfPTable(5);
				table5.setWidthPercentage(100);
				
				PdfPCell row5 = new PdfPCell(new Paragraph("Account Ref.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("P.O. No.",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Terms",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Due Date",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph("Project Name",smallBoldWhite));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(greenColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				//second 
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("ACCOUNT_REF"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PO_NO"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("TERMS"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("BILL_DUE_DATE"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_CENTER);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row5 = new PdfPCell(new Paragraph(uF.showData(hmProjectDetails.get("PRO_NAME"), ""),small));
				row5.setHorizontalAlignment(Element.ALIGN_LEFT);
				row5.setBorder(Rectangle.NO_BORDER);
				row5.setBackgroundColor(backgroundColor);
				row5.setPadding(2.5f);
				table5.addCell(row5);
				
				row1 = new PdfPCell(table5);
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table6 = new PdfPTable(5);
//			table6.setWidthPercentage(100);
			float[] columnWidths = new float[] {6f, 49f, 15f, 15f, 15f};
            table6.setWidths(columnWidths);
			
			PdfPCell row6 = new PdfPCell(new Paragraph("#",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Description",smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			String strCalType = "";
			if(hmProjectDetails.get("CALCULATION_TYPE") != null && hmProjectDetails.get("CALCULATION_TYPE").trim().equalsIgnoreCase("H")){
				strCalType = "Hours";
			} else if(hmProjectDetails.get("CALCULATION_TYPE") != null && hmProjectDetails.get("CALCULATION_TYPE").trim().equalsIgnoreCase("D")){
				strCalType = "Days";
			} else if(hmProjectDetails.get("CALCULATION_TYPE") != null && hmProjectDetails.get("CALCULATION_TYPE").trim().equalsIgnoreCase("M")){
				strCalType = "Months";
			}
			
			row6 = new PdfPCell(new Paragraph("Qty/"+strCalType,smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Rate"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row6 = new PdfPCell(new Paragraph("Amount"+uF.showData(currency, ""),smallBoldWhite));
			row6.setHorizontalAlignment(Element.ALIGN_CENTER);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(greenColor);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			
			
			//second 
			
			int pertiSize = 0;
			
			for (int i = 0,k=1; empAmtList != null && i < empAmtList.size(); i++,k++) {
				Map<String, String> hmInner = empAmtList.get(i);
				if (hmInner == null) hmInner = new HashMap<String, String>();

//				if (uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMOUNT")) == 0.0) {
//					continue;
//				}
			
				row6 = new PdfPCell(new Paragraph(k+". ",small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				String empName = uF.showData(hmInner.get("EMP_NAME"), "");
				row6 = new PdfPCell(new Paragraph(empName,small));
				row6.setHorizontalAlignment(Element.ALIGN_LEFT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(hmInner.get("DAYS_HOURS"), ""),small));
				row6.setHorizontalAlignment(Element.ALIGN_CENTER);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.showData(hmInner.get("RATE"), ""),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				row6 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMOUNT"))),small));
				row6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row6.setBorder(Rectangle.NO_BORDER);
				row6.setBackgroundColor(backgroundColor);
				row6.setPadding(2.5f);
				table6.addCell(row6);
				
				pertiSize++;
			}
			
			//second new row
			
			String strLines = "";
			for (int i = pertiSize; i < 27; i++) {
				strLines = strLines + "\n";
			}
			
			row6 = new PdfPCell(new Paragraph(strLines,small));
			row6.setHorizontalAlignment(Element.ALIGN_LEFT);
			row6.setBorder(Rectangle.NO_BORDER);
			row6.setBackgroundColor(backgroundColor);
			row6.setColspan(5);
			row6.setPadding(2.5f);
			table6.addCell(row6);
			
			row1 = new PdfPCell(table6);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table7 = new PdfPTable(5);
			table7.setWidthPercentage(100);
			
			PdfPCell row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Subtotal",smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//second
			row7 = new PdfPCell(new Paragraph("Other Fees / Taxes",smallBoldWhite));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(greenColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//third
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				
				row7 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), ""),small));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setColspan(3);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph("",smallBold));
				row7.setHorizontalAlignment(Element.ALIGN_LEFT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
				
				row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))),small));
				row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row7.setBorder(Rectangle.NO_BORDER);
				row7.setBackgroundColor(backgroundColor);
				row7.setPadding(2.5f);
				table7.addCell(row7);
			}
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph("Total"+uF.showData(currency, ""),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row7 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))),smallBold));
			row7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row7.setBorder(Rectangle.BOTTOM);
			row7.setBackgroundColor(backgroundColor);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			//fourth
			row7 = new PdfPCell(new Paragraph("",small));
			row7.setHorizontalAlignment(Element.ALIGN_CENTER);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(3);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			double totalAmt=uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"));
			String digitTotal="";
	        String strTotalAmt=""+totalAmt;
	        if(strTotalAmt.contains(".")){
	        	strTotalAmt=strTotalAmt.replace(".", ",");
	        	String[] temp=strTotalAmt.split(",");
	        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
	        	if(uF.parseToInt(temp[1])>0){
	        		int pamt=0;
	        		if(temp[1].length()==1){
	        			pamt=uF.parseToInt(temp[1]+"0");
	        		}else{
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" "+uF.showData(hmCurr.get("CURR_SUB_DIVISION"), "");
	        	}
	        }else{
	        	int totalAmt1=(int)totalAmt;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
			
			row7 = new PdfPCell(new Paragraph(""+digitTotal,small));
			row7.setHorizontalAlignment(Element.ALIGN_LEFT);
			row7.setBorder(Rectangle.NO_BORDER);
			row7.setBackgroundColor(backgroundColor);
			row7.setColspan(5);
			row7.setPadding(2.5f);
			table7.addCell(row7);
			
			row1 = new PdfPCell(table7);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			if(uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")) > 0) {
				//New Row
				row1 = new PdfPCell(new Paragraph("Payment Mode:", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				//New Row       
				//New Row       
				StringBuilder sbBankAccData = new StringBuilder();
				StringBuilder sbBankAccData1 = new StringBuilder();
				StringBuilder sbBankAccData2 = new StringBuilder();
				sbBankAccData.append("Bank Details: \n");
				sbBankAccData.append("A/C No.: "+hmBankBranch.get("BRANCH_ACCOUNT_NO")+"\n");
				sbBankAccData.append("Branch: "+hmBankBranch.get("BRANCH_BRANCH")+"\n");
				sbBankAccData.append("Bank: "+hmBankBranch.get("BRANCH_BANK_NAME")+"\n");
				sbBankAccData1.append("\n");
				if(hmBankBranch.get("BRANCH_IFSC_CODE") != null && !hmBankBranch.get("BRANCH_IFSC_CODE").equals("")) {
					sbBankAccData1.append("IFSC: "+hmBankBranch.get("BRANCH_IFSC_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_SWIFT_CODE") != null && !hmBankBranch.get("BRANCH_SWIFT_CODE").equals("")) {
					sbBankAccData1.append("SWIFT: "+hmBankBranch.get("BRANCH_SWIFT_CODE")+"\n");
				}
				if(hmBankBranch.get("BRANCH_CLEARING_CODE") != null && !hmBankBranch.get("BRANCH_CLEARING_CODE").equals("")) {
					sbBankAccData1.append("BCC: "+hmBankBranch.get("BRANCH_CLEARING_CODE")+"\n");
				}
				sbBankAccData2.append("\n");
				if(hmBankBranch.get("OTHER_INFO") != null && !hmBankBranch.get("OTHER_INFO").equals("")) {
					sbBankAccData2.append(hmBankBranch.get("OTHER_INFO")+"\n");
				}
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData1.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph(uF.showData(sbBankAccData2.toString(), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(2);
				row1.setPadding(2.5f);
//				row1.setIndent(10.0f);
				table.addCell(row1);
			}
			
			//New Row
			if(hmProjectDetails.get("PAYPAL_MAIL_ID") !=null && !hmProjectDetails.get("PAYPAL_MAIL_ID").trim().equals("")){
				row1 = new PdfPCell(new Paragraph("Paypal- "+uF.showData(hmProjectDetails.get("PAYPAL_MAIL_ID"), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.NO_BORDER);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				row1.setIndent(10.0f);
				table.addCell(row1);
			}
			
			//New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setIndent(10.0f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("\n\nAuthorised Signatory,", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph("This is computer generated statement and does not need signature.", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setRowspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//New Row
			//first
			PdfPTable table8 = new PdfPTable(5);
			table8.setWidthPercentage(100);
			
			PdfPCell row8 = new PdfPCell(new Paragraph("Phone No.",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("E-mail",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph("Web Site",smallBoldWhite));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(greenColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			
			
			//second 
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_CONTACT"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_EMAIL"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row8 = new PdfPCell(new Paragraph(uF.showData(hmOrgData.get("ORG_WEBSITE"), ""),small));
			row8.setHorizontalAlignment(Element.ALIGN_CENTER);
			row8.setBorder(Rectangle.NO_BORDER);
			row8.setBackgroundColor(backgroundColor);
			row8.setColspan(2);
			row8.setPadding(2.5f);
			table8.addCell(row8);
			
			row1 = new PdfPCell(table8);
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			/*String strPath = request.getRealPath("/images1/icons/icons/taskrig.png");
			System.out.println("request.getRealPath()======>"+request.getRealPath("/images1/icons/icons/taskrig.png"));
			System.out.println("request.getContextPath()======>"+request.getContextPath()+"/images1/icons/icons/taskrig.png");
			String strPoweredBy = "Powered by <img src='"+strPath+"' height=\"25\">";
			List<Element> al2 = HTMLWorker.parseToList(new StringReader(strPoweredBy), null);
			Paragraph pr2 = new Paragraph("",small);
			pr2.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr2));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);*/
			
			document.add(table);
			
//			Image imageProductLogo=Image.getInstance(request.getRealPath("/images1/icons/icons/taskrig.png"));
//			imageProductLogo.setAbsolutePosition(445, 0);
//			imageProductLogo.scaleToFit(100, 100);
//			document.add(imageProductLogo);
			
			document.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	private String getProCurrencyType(UtilityFunctions uF,int pro_id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String currId = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select billing_curr_id from projectmntnc where pro_id = ?");
			pst.setInt(1, pro_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				currId = rs.getString("billing_curr_id");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return currId;
	}
	private String getCurrencyType(UtilityFunctions uF,String invoice_id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String currId = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select curr_id from promntc_invoice_details where promntc_invoice_id = ?");
			pst.setInt(1, uF.parseToInt(invoice_id));
			rs = pst.executeQuery();
			while (rs.next()) {
				currId = rs.getString("curr_id");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return currId;
	}
	
	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	private HttpServletRequest request;

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getInvDate() {
		return invDate;
	}

	public void setInvDate(String invDate) {
		this.invDate = invDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}
	
}
