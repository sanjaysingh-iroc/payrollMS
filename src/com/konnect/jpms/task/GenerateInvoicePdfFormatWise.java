package com.konnect.jpms.task;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;

public class GenerateInvoicePdfFormatWise implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	HttpSession session;
	String invDate;
	String paycycle;
	int pro_id;
	String invoice_id;
	String operation;
	int teamsize;
	String sessionEmpId;
	CommonFunctions CF; 
	String type;

	public void execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;

		sessionEmpId = (String) session.getAttribute(EMPID);

		String currType = getCurrencyType(getInvoice_id());
		String proCurrType = getProCurrencyType(getPro_id());
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("getType() ===>> " + getType());
//		System.out.println("proCurrType ===>> " + proCurrType +"  currType ====>> " + currType );
		if (uF.parseToInt(getType()) == ADHOC_INVOICE) {
			if (currType != null && currType.equals("3")) {
				generateProjectAdHocPdfReport();
			} else {
				generateProjectAdHocPdfReportOtherCurr();
			}
		} else if (uF.parseToInt(getType()) == ADHOC_PRORETA_INVOICE) {
			if (currType != null && currType.equals("3")) {
				generateProjectProRetaPdfReport();
			} else {
				generateProjectProRetaPdfReportOtherCurr();
			}
		} else if (uF.parseToInt(getType()) == PRORETA_INVOICE) {
//			System.out.println("getType() in 5 ===>> " + getType());
			if (currType != null && currType.equals(proCurrType)) {
				generateProjectProRetaPdfReport();
			} else {
				generateProjectProRetaPdfReportOtherCurr();
			}
		} else {
			if (currType != null && currType.equals(proCurrType)) {
				generateProjectPdfReport();
			} else {
				generateProjectPdfReportOtherCurr();
			}
			// generateProjectReport();
			session.removeAttribute("pro_id");
		}
		// return null;
	}

	private String getProCurrencyType(int pro_id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
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

	private String getCurrencyType(String invoice_id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
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

	
	
	private void generateProjectProRetaPdfReport() {

		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
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
				hmInvoiceDetails.put("ADDRESS_ID", rs.getString("address_id"));
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
				hmInvoiceDetails.put("BILL_TYPE", rs.getString("bill_type"));
				hmInvoiceDetails.put("INVOICE_TYPE", rs.getString("invoice_type"));
				
				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));

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

			/*pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmInvoiceAmtDetails = new LinkedHashMap<String, Map<String, String>>();
			List<List<String>> outerAmtList = new ArrayList<List<String>>();
			List<Map<String, String>> empAmtList = new ArrayList<Map<String, String>>();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("emp_id")) == 0 && (rs.getString("resource_name") == null || rs.getString("resource_name").equals(""))) {
					Map<String, String> hmInner = hmInvoiceAmtDetails.get(rs.getString("promntc_invoice_id"));
					if (hmInner == null)
						hmInner = new HashMap<String, String>();
					hmInner.put("PROJECT_INVOICE_AMT_ID", rs.getString("promntc_invoice_amt_id"));
					hmInner.put("INVOICE_PARTICULARS", rs.getString("invoice_particulars"));
					hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
					hmInner.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));

					hmInvoiceAmtDetails.put(rs.getString("promntc_invoice_id"), hmInner);

					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("promntc_invoice_amt_id"));
					innerList.add(rs.getString("invoice_particulars"));
					innerList.add(rs.getString("oc_invoice_particulars_amount"));
					innerList.add(rs.getString("promntc_invoice_id"));

					outerAmtList.add(innerList);
				} else {
					Map<String, String> hmInner = new HashMap<String, String>();
//					System.out.println("INVOICE_TYPE ====>> " + hmInvoiceDetails.get("INVOICE_TYPE"));
					if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TYPE")) == ADHOC_PRORETA_INVOICE) {
//						hmInner.put("EMP_ID", rs.getString("emp_id"));
						hmInner.put("EMP_NAME", rs.getString("resource_name"));
						hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
						hmInner.put("RATE", rs.getString("_rate"));
						hmInner.put("DAY_OR_HOUR", rs.getString("day_or_hour"));
						hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
					} else {
						hmInner.put("EMP_ID", rs.getString("emp_id"));
						hmInner.put("EMP_NAME", hmEmpName.get(rs.getString("emp_id")));
						hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
						hmInner.put("RATE", rs.getString("_rate"));
						hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
					}
					empAmtList.add(hmInner);
				}

			}
			rs.close();
			pst.close();*/
			
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
					hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
				} else {
					hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("EMP_NAME", hmEmpName.get(rs.getString("emp_id")));
					hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
					hmInner.put("RATE", rs.getString("_rate"));
					hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
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
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from client_address where client_address_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("ADDRESS_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(",", ",\n") : "";
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

//			pst = con.prepareStatement("select * from deduction_tax_misc_details where ? between financial_year_from and financial_year_to and trail_status = 1");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			Map<String, String> hmTaxMiscSetting = new HashMap<String, String>();
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

			pst = con.prepareStatement("select bd1.bank_name,bd.branch_id,bd.branch_code,bd.bank_description,bd.bank_address,bd.bank_city,bd.bank_state_id,"
					+ "bd.bank_country_id,bd.bank_branch,bd.bank_email,bd.bank_fax,bd.bank_contact,bd.bank_ifsc_code,bd.bank_account_no,"
					+ " bd.bank_pincode,bd.bank_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id "
					+ " and bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("bank_name"));
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
				hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();

			InvoiceFormatwiseData invoiceFormatwiseData = new InvoiceFormatwiseData(request, session, CF, uF, con, getPro_id()+"");
			Map<String, String> hmInvoiceFormatData = invoiceFormatwiseData.getInvoiceFormatDataPDF(getInvoice_id());
//			System.out.println("hmInvoiceFormatData ===>>>> " + hmInvoiceFormatData);
			
			ByteArrayOutputStream buffer = generateProRetaPdfDocument(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, hmDesignation, 
					client_id, outerAmtList, outerTaxList, hmProjectDetails, hmCurrencyDetails, empAmtList, hmInvoiceFormatData);
			
			if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {

				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();

			} else {
				byte[] bytes = buffer.toByteArray();
				Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setSupervisor(true); // nF.setStrEmpId(sessionEmpId);
				nF.setStrEmailTo("");
				nF.setSupervisor(true);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("ProjectSummary.pdf");
				nF.sendNotifications();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
	private void generateProjectProRetaPdfReportOtherCurr() {

		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			// Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);

			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);

			pst = con.prepareStatement(" select * from promntc_invoice_details where pro_id = ? and promntc_invoice_id=?");
			pst.setInt(1, getPro_id());
			pst.setInt(2, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			String inrValue = null;
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			while (rs.next()) {
				
				inrValue = getExchangeValue(uF, rs.getString("curr_id"));
				double dblOCInvoiceAmt = 0;
				double dblOCPartiTotAmt = 0;
				double dblOCOtherAmt = 0;
				
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
				hmInvoiceDetails.put("ADDRESS_ID", rs.getString("address_id"));
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
				hmInvoiceDetails.put("BILL_TYPE", rs.getString("bill_type"));
				hmInvoiceDetails.put("INVOICE_TYPE", rs.getString("invoice_type"));
				
				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));

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

			
//			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? ");
//			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
//			rs = pst.executeQuery();
//			Map<String, Map<String, String>> hmInvoiceAmtDetails = new LinkedHashMap<String, Map<String, String>>();
//			List<List<String>> outerAmtList = new ArrayList<List<String>>();
//			List<Map<String, String>> empAmtList = new ArrayList<Map<String, String>>();
//			while (rs.next()) {
//				double dblOCPartiAmt = 0;
//				if (uF.parseToInt(rs.getString("emp_id")) == 0 && (rs.getString("resource_name") == null || rs.getString("resource_name").equals(""))) {
//					Map<String, String> hmInner = hmInvoiceAmtDetails.get(rs.getString("promntc_invoice_id"));
//					if (hmInner == null)
//						hmInner = new HashMap<String, String>();
//					hmInner.put("PROJECT_INVOICE_AMT_ID", rs.getString("promntc_invoice_amt_id"));
//					hmInner.put("INVOICE_PARTICULARS", rs.getString("invoice_particulars"));
//					
////					dblOCPartiAmt = uF.parseToDouble(rs.getString("invoice_particulars_amount")) / uF.parseToDouble(inrValue);
//					hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
//					
//					hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
//					hmInner.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
//
//					hmInvoiceAmtDetails.put(rs.getString("promntc_invoice_id"), hmInner);
//
//					List<String> innerList = new ArrayList<String>();
//					innerList.add(rs.getString("promntc_invoice_amt_id"));
//					innerList.add(rs.getString("invoice_particulars"));
//					innerList.add(rs.getString("invoice_particulars_amount"));
//					innerList.add(rs.getString("oc_invoice_particulars_amount"));
//					innerList.add(rs.getString("promntc_invoice_id"));
//
//					outerAmtList.add(innerList);
//				} else {
//
//					Map<String, String> hmInner = new HashMap<String, String>();
//					if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TYPE")) == ADHOC_PRORETA_INVOICE) {
////						hmInner.put("EMP_ID", rs.getString("emp_id"));
//						hmInner.put("EMP_NAME", rs.getString("resource_name"));
//						hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
//						hmInner.put("RATE", rs.getString("_rate"));
//						hmInner.put("DAY_OR_HOUR", rs.getString("day_or_hour"));
//						hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
//						
////						dblOCPartiAmt = uF.parseToDouble(rs.getString("invoice_particulars_amount")) / uF.parseToDouble(inrValue);
//						hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
//					} else {
//						hmInner.put("EMP_ID", rs.getString("emp_id"));
//						hmInner.put("EMP_NAME", hmEmpName.get(rs.getString("emp_id")));
//						hmInner.put("DAYS_HOURS", rs.getString("days_hours"));
//						hmInner.put("RATE", rs.getString("_rate"));
//						hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
//						
////						dblOCPartiAmt = uF.parseToDouble(rs.getString("invoice_particulars_amount")) / uF.parseToDouble(inrValue);
//						hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
//					}
//					empAmtList.add(hmInner);
//				}
//
//			}
//			rs.close();
//			pst.close();
			
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
			
			
			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type is null");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			List<Map<String, String>> empAmtList = new ArrayList<Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TYPE")) == ADHOC_PRORETA_INVOICE) {
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
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from client_address where client_address_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("ADDRESS_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(",", ",\n") : "";
				// client_address=rs.getString("client_address");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from deduction_tax_misc_details where ? between financial_year_from and financial_year_to and trail_status = 1");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			Map<String, String> hmTaxMiscSetting = new HashMap<String, String>();
			while (rs.next()) {
				hmTaxMiscSetting.put("DEDUCTION_TAX_MISC_ID", rs.getString("deduction_tax_misc_id"));
				hmTaxMiscSetting.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmTaxMiscSetting.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmTaxMiscSetting.put("FLAT_TDS", rs.getString("flat_tds"));
				hmTaxMiscSetting.put("SERVICE_TAX", rs.getString("service_tax"));
				hmTaxMiscSetting.put("DEDUCTION_TYPE", rs.getString("deduction_type"));
				hmTaxMiscSetting.put("FINANCIAL_YEAR_FROM", rs.getString("financial_year_from"));
				hmTaxMiscSetting.put("FINANCIAL_YEAR_TO", rs.getString("financial_year_to"));
				hmTaxMiscSetting.put("YEAR", rs.getString("_year"));
				hmTaxMiscSetting.put("ENTRY_TIMESTAMP", rs.getString("entry_timestamp"));
				hmTaxMiscSetting.put("USER_ID", rs.getString("user_id"));
				hmTaxMiscSetting.put("ACTIVITY", rs.getString("trail_status"));
				hmTaxMiscSetting.put("STATE_ID", rs.getString("state_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select bd1.bank_name,bd.branch_id,bd.branch_code,bd.bank_description,bd.bank_address,bd.bank_city,bd.bank_state_id,"
					+ "bd.bank_country_id,bd.bank_branch,bd.bank_email,bd.bank_fax,bd.bank_contact,bd.bank_ifsc_code,bd.bank_account_no,"
					+ " bd.bank_pincode,bd.bank_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id "
					+ " and bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("bank_name"));
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
				hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();

			InvoiceFormatwiseData invoiceFormatwiseData = new InvoiceFormatwiseData(request, session, CF, uF, con, getPro_id()+"");
			Map<String, String> hmInvoiceFormatData = invoiceFormatwiseData.getInvoiceFormatDataPDF(getInvoice_id());
//			System.out.println("hmInvoiceFormatData ===>>>> " + hmInvoiceFormatData);
			
			ByteArrayOutputStream buffer = generateProRetaPdfDocumentOtherCurr(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, 
					hmDesignation, client_id, outerAmtList, outerTaxList, hmProjectDetails, hmTaxMiscSetting, hmCurrencyDetails, empAmtList, hmInvoiceFormatData);
			
			if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {

				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();

			} else {
				byte[] bytes = buffer.toByteArray();
				Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setSupervisor(true); // nF.setStrEmpId(sessionEmpId);
				nF.setStrEmailTo("");
				nF.setSupervisor(true);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("ProjectSummary.pdf");
				nF.sendNotifications();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}
	

	
	private ByteArrayOutputStream generateProRetaPdfDocument(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails, 
		Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String client_id, 
		List<List<String>> outerAmtList, List<List<String>> outerTaxList, Map<String, String> hmProjectDetails, 
		Map<String, Map<String, String>> hmCurrencyDetails, List<Map<String, String>> empAmtList, Map<String, String> hmInvoiceFormatData) {

		System.out.println("generateProRetaPdfDocument ======");
		String RESULT = "/home/user/Desktop/FormNo10.pdf";
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normal1 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font small1 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {

//			Map<String, String> hmOrgInner = CF.getEmpOrgDetails(con, uF, hmProjectOwnerDetails.get("EMP_ID"));

			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null)
				hmCurr = new HashMap<String, String>();

			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);

			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("  Original  /  Cenvat Copy  /  Office Copy  ", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row

			String strelement = uF.showData(hmInvoiceFormatData.get("SECTION_1"), "");
//			strelement = strelement.replace("<p>", "");
//			strelement = strelement.replace("</p>", "");
			
			List<Element> al = HTMLWorker.parseToList(new StringReader(strelement), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);  
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			List<Element> al2 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_2"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			List<Element> al3 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_3"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al3);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			List<Element> al4 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_4"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al4);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al5 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_5"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al5);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al6 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_6"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al6);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			// New Row
			row1 = new PdfPCell(new Paragraph("P A R T I C U L A R S", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			String currency = hmCurr.get("SHORT_CURR") != null && !hmCurr.get("SHORT_CURR").equals("") ? " (" + hmCurr.get("SHORT_CURR") + ")" : "";
			row1 = new PdfPCell(new Paragraph("AMOUNT " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("AMOUNT " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			// row1 = new PdfPCell(new
			// Paragraph("(Billing period taken as "+uF.showData(hmInvoiceDetails.get("INVOICE_FROM_DATE"),
			// "")+" - "+uF.showData(hmInvoiceDetails.get("INVOICE_TO_DATE"),
			// "")+")", small));
			// row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			// row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			// row1.setColspan(4);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);

			
			// New Row
			int pertiSize = 0;
			pertiSize += (hmInvoiceDetails.get("REFERENCE_NO_DESC") != null && !hmInvoiceDetails.get("REFERENCE_NO_DESC").equals("")) ? (hmInvoiceDetails.get("REFERENCE_NO_DESC").length() / 60) : 0;
			
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("REFERENCE_NO_DESC"), "")+"\n\n", small));
			 row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 row1.setColspan(4);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 
			// New Row loop
			for (int i = 0; empAmtList != null && i < empAmtList.size(); i++) {
				Map<String, String> hmInner = empAmtList.get(i);
				if (hmInner == null)
					hmInner = new HashMap<String, String>();

				if (uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMOUNT")) == 0.0) {
					continue;
				}
				String empName = uF.showData(hmInner.get("EMP_NAME"), "");

				row1 = new PdfPCell(new Paragraph(empName, smallBold));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				String dayOrHour = "";
				if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TYPE")) == ADHOC_PRORETA_INVOICE) {
					if(uF.parseToInt(hmInner.get("DAY_OR_HOUR")) == 1) {
						dayOrHour = "Days";
					} else if(uF.parseToInt(hmInner.get("DAY_OR_HOUR")) == 2) {
						dayOrHour = "Hours";
					}  else if(uF.parseToInt(hmInner.get("DAY_OR_HOUR")) == 3) {
						dayOrHour = "Months";
					}
				} else {
					dayOrHour = uF.showData(hmInvoiceDetails.get("BILL_TYPE"), "");
				}
				
				String empCostData = uF.showData(hmInner.get("DAYS_HOURS"), "") + " "+ dayOrHour + " @ " + uF.showData(hmInner.get("RATE"), "");

				row1 = new PdfPCell(new Paragraph(empCostData, smallBold));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMOUNT"))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				pertiSize++;

			}

			// New Row loop

			for (int i = 0; outerAmtList != null && i < outerAmtList.size(); i++) {
				List<String> innerList = outerAmtList.get(i);
				if (uF.parseToDouble(innerList.get(2)) == 0.0) {
					continue;
				}
				row1 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(4);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				pertiSize++;
			}

			// New Row
//			if (uF.parseToDouble(hmInvoiceDetails.get("OTHER_AMOUNT")) > 0.0) {
//
//				row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_PARTICULAR"), ""), small));
//				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//				row1.setColspan(4);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//
//				row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(hmInvoiceDetails.get("OTHER_AMOUNT"))), small));
//				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//				row1.setBorder(Rectangle.RIGHT);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//
//				row1 = new PdfPCell(new Paragraph("", small));
//				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//				row1.setBorder(Rectangle.RIGHT);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//			}

			// New Row

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("" + uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))), small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// //New Row
			// row1 = new PdfPCell(new
			// Paragraph(uF.showData(hmProjectDetails.get("PRO_NAME"),
			// "")+"\n"+uF.showData(hmProjectDetails.get("DESCRIPTION"),
			// ""), small));
			// row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			// row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			// row1.setColspan(4);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// //New Row
			// row1 = new PdfPCell(new
			// Paragraph(uF.showData(hmInvoiceDetails.get("PROJECT_DESCRIPTION"),
			// ""), small));
			// row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			// row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			// row1.setColspan(4);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);

			
			// New Row
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				if (uF.parseToDouble(innerList.get(2)) == 0.0) {
					continue;
				}
//				row1 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), "")+" @ " + uF.showData(innerList.get(5), "0") + "%", small));
				row1 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(4);
				row1.setPadding(2.5f);
				table.addCell(row1);
	
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
	
				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				pertiSize++;
			}
			
			
//			// New Row
//			row1 = new PdfPCell(new Paragraph("Add: Service Tax @ " + uF.showData(hmInvoiceDetails.get("SERVICE_TAX"), "0") + "%", small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(sTax)), small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("Add: Educational Cess @ " + uF.showData(hmInvoiceDetails.get("EDUCATION_TAX"), "0") + "%", small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(eduCess)), small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("Add: Secondary and Higher Secondary Cess @ " + uF.showData(hmInvoiceDetails.get("STANDARD_TAX"), "0") + "%",
//					small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(shsCess)), small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);

			/*
			 * //New Row row1 = new PdfPCell(new
			 * Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"),
			 * ""), small)); row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 * row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 * row1.setColspan(4); row1.setPadding(2.5f); table.addCell(row1);
			 * 
			 * row1 = new PdfPCell(new Paragraph("", small));
			 * row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * row1.setBorder(Rectangle.RIGHT); row1.setPadding(2.5f);
			 * table.addCell(row1);
			 * 
			 * row1 = new PdfPCell(new Paragraph("", small));
			 * row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * row1.setBorder(Rectangle.RIGHT); row1.setPadding(2.5f);
			 * table.addCell(row1);
			 */

			// New Row
			double totalAmt = uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"));
			String digitTotal = "";
			String strTotalAmt = "" + totalAmt;
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
				int totalAmt1 = (int) totalAmt;
				digitTotal = uF.digitsToWords(totalAmt1);
			}

			row1 = new PdfPCell(new Paragraph("\n\n" + digitTotal + " only\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("Tax is NOT required to be deducted on service tax portion\n\n", italicEffect));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), "") + "\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("TOTAL " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

//			// New Row
//			String bankDetails = uF.showData(hmBankBranch.get("BRANCH_BANK_NAME"), "") + ", " + uF.showData(hmBankBranch.get("BRANCH_ADDRESS"), "") + ", "
//					+ uF.showData(hmBankBranch.get("BRANCH_CITY"), "") + ",\nA/c No. " + uF.showData(hmBankBranch.get("BRANCH_ACCOUNT_NO"), "")
//					+ ", IFSC code- " + uF.showData(hmBankBranch.get("BRANCH_IFSC_CODE") + "\n\n", "");
//			row1 = new PdfPCell(new Paragraph("Bank Details: " + bankDetails, smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph(
////				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + "Chartered Accountants\n\n\n\n\n"
//				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + uF.showData(hmOrgInner.get("ORG_SUB_TITLE"), "") +"\n\n\n\n\n"
//				+ uF.showData(hmProjectOwnerDetails.get("EMP_NAME"), "") + "\n\n"
//				+ uF.showData(hmDesignation.get(hmProjectOwnerDetails.get("EMP_ID")), ""), smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(3);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("                     " + uF.showData(hmWlocation.get("WL_ECC1_NO"), "-")
//					+ "\nECC Code: ------------------------\n" + "                     " + uF.showData(hmWlocation.get("WL_ECC2_NO"), "-") + "\n\nPAN: "
////					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmWlocation.get("WL_REG_NO"), "") + "\n\n", smallBold));
//					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmOrgInner.get("ORG_REG_NO"), "") + "\n\n", smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(3);
//			table.addCell(row1);
//
//
//			// New Row
//			if (wLocation != null && wLocation.contains(",")) {
//				wLocation = wLocation.substring(0, wLocation.lastIndexOf(",")) + " and " + wLocation.substring(wLocation.lastIndexOf(",") + 1);
//			}
//			row1 = new PdfPCell(new Paragraph(wLocation, smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			row1.setFixedHeight(0f);
//			table.addCell(row1);

			// New Row
			List<Element> al7 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_7"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al7);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al8 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_8"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al8);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al9 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_9"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al9);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al10 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_10"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al10);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al11 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_11"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al11);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setFixedHeight(0f);
			table.addCell(row1);
			
			document.add(table);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}
	
	
	
	private ByteArrayOutputStream generateProRetaPdfDocumentOtherCurr(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails, 
		Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String client_id, 
		List<List<String>> outerAmtList, List<List<String>> outerTaxList, Map<String, String> hmProjectDetails, Map<String, String> hmTaxMiscSetting, 
		Map<String, Map<String, String>> hmCurrencyDetails, List<Map<String, String>> empAmtList, 
		Map<String, String> hmInvoiceFormatData) {

		String RESULT = "/home/user/Desktop/FormNo10.pdf";
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normal1 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font small1 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try { 

//			Map<String, String> hmOrgInner = CF.getEmpOrgDetails(con, uF, hmProjectOwnerDetails.get("EMP_ID"));

			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null)
				hmCurr = new HashMap<String, String>();
			
			String proCurrId = "3";
			if(uF.parseToInt(hmProjectDetails.get("PRO_CURR_ID")) > 0) {
				proCurrId = hmProjectDetails.get("PRO_CURR_ID");
			}
			Map<String, String> hmProCurr = hmCurrencyDetails.get(proCurrId);
			if (hmProCurr == null)
				hmProCurr = new HashMap<String, String>();

			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			table.setFooterRows(25);

			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("  Original  /  Cenvat Copy  /  Office Copy  ", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			String strelement = uF.showData(hmInvoiceFormatData.get("SECTION_1"), "");
			List<Element> al = HTMLWorker.parseToList(new StringReader(strelement), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al2 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_2"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			List<Element> al3 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_3"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al3);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al4 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_4"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al4);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al5 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_5"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al5);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			List<Element> al6 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_6"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al6);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			row1 = new PdfPCell(new Paragraph("P A R T I C U L A R S", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			String currency = hmCurr.get("LONG_CURR") != null && !hmCurr.get("LONG_CURR").equals("") ? " (" + hmCurr.get("LONG_CURR") + ")" : "";
			String proCurrency = hmProCurr.get("LONG_CURR") != null && !hmProCurr.get("LONG_CURR").equals("") ? " (" + hmProCurr.get("LONG_CURR") + ")" : "";
			row1 = new PdfPCell(new Paragraph(proCurrency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			int pertiSize = 0;
			pertiSize += (hmInvoiceDetails.get("REFERENCE_NO_DESC") != null && !hmInvoiceDetails.get("REFERENCE_NO_DESC").equals("")) ? (hmInvoiceDetails.get("REFERENCE_NO_DESC").length() / 60) : 0;
			
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("REFERENCE_NO_DESC"), "")+"\n\n", small));
			 row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 row1.setColspan(4);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);

			 
			// New Row loop
			for (int i = 0; empAmtList != null && i < empAmtList.size(); i++) {
				Map<String, String> hmInner = empAmtList.get(i);
				if (hmInner == null)
					hmInner = new HashMap<String, String>();

				if (uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMOUNT")) == 0.0) {
					continue;
				}
				String empName = uF.showData(hmInner.get("EMP_NAME"), "");
				row1 = new PdfPCell(new Paragraph(empName, smallBold));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				String dayOrHour = "";
				if(uF.parseToInt(hmInvoiceDetails.get("INVOICE_TYPE")) == ADHOC_PRORETA_INVOICE) {
					if(uF.parseToInt(hmInner.get("DAY_OR_HOUR")) == 1) {
						dayOrHour = "Days";
					} else if(uF.parseToInt(hmInner.get("DAY_OR_HOUR")) == 2) {
						dayOrHour = "Hours";
					}
				} else {
					dayOrHour = uF.showData(hmInvoiceDetails.get("BILL_TYPE"), "");
				}
				
				String empCostData = uF.showData(hmInner.get("DAYS_HOURS"), "") + " "+ dayOrHour + " @ " + uF.showData(hmInner.get("RATE"), "");

				row1 = new PdfPCell(new Paragraph(empCostData, smallBold));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setColspan(2);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(hmInner.get("INVOICE_PARTICULARS_AMOUNT"))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(hmInner.get("OC_INVOICE_PARTICULARS_AMOUNT"))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);

			}

			
			// New Row loop

			for (int i = 0; outerAmtList != null && i < outerAmtList.size(); i++) {
				List<String> innerList = outerAmtList.get(i);
				String partiAmount = "";
				String partiOCAmount = "";
				if (uF.parseToDouble(innerList.get(2)) > 0.0) {
					partiAmount = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2)));
					partiOCAmount = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3)));
				} else {
					partiAmount = "\n";
					partiOCAmount = "\n";
				}
				
				row1 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(4);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph(partiAmount, small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph(partiOCAmount, small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				pertiSize++;
			}
			
			
			// New Row
//			if (uF.parseToDouble(hmInvoiceDetails.get("OTHER_AMOUNT")) > 0.0) {
//
//				row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_PARTICULAR"), ""), small));
//				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//				row1.setColspan(4);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//
//				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OTHER_AMOUNT"))), small));
//				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//				row1.setBorder(Rectangle.RIGHT);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//
//				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_OTHER_AMOUNT"))), small));
//				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//				row1.setBorder(Rectangle.RIGHT);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//			}

//New Row
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))), small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			//System.out.println("OC_PARTICULARS_TOTAL_AMOUNT =======>> " + hmInvoiceDetails.get("OC_PARTICULARS_TOTAL_AMOUNT"));
			row1 = new PdfPCell(new Paragraph(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_PARTICULARS_TOTAL_AMOUNT"))), small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			// New Row
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				if (uF.parseToDouble(innerList.get(2)) == 0.0) {
					continue;
				}
				row1 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), "")+" @ " + uF.showData(innerList.get(6), "0") + "%", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(4);
				row1.setPadding(2.5f);
				table.addCell(row1);
			
				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
			
				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				pertiSize++;
			}

			
//			// New Row
//			String bankDetails = uF.showData(hmBankBranch.get("BRANCH_BANK_NAME"), "") + ", " + uF.showData(hmBankBranch.get("BRANCH_ADDRESS"), "") + ", "
//			+ uF.showData(hmBankBranch.get("BRANCH_CITY"), "") 
////			+ ",\nA/c No. " + uF.showData(hmBankBranch.get("BRANCH_ACCOUNT_NO"), "")
////			+ ", IFSC code- " + uF.showData(hmBankBranch.get("BRANCH_IFSC_CODE") + "\n\n", "") 
//			+ "\n Swift code: " + uF.showData(hmBankBranch.get("BRANCH_SWIFT_CODE"), "")
//			+ "\n Bank Clearning code: " + uF.showData(hmBankBranch.get("BRANCH_BANK_CLEARING_CODE") , "") + "\n\n";
//			row1 = new PdfPCell(new Paragraph("PLEASE PAY TO \n" + bankDetails, small)); // Bank Details
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph(uF.showData(hmBankBranch.get("BRANCH_OTHER_INFO"), ""), small)); // Bank Other Details
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);

			// New Row
			double totalAmt = uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"));
			// System.out.println("totalAmt ===>>>> " + totalAmt);

			String digitTotal = "";
			String strTotalAmt = "" + uF.formatIntoTwoDecimalWithOutComma(totalAmt);

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
					digitTotal += " and" + uF.digitsToWords(pamt) + " cents";
				}
			} else {
				int totalAmt1 = (int) totalAmt;
				digitTotal = uF.digitsToWords(totalAmt1);
			}

			row1 = new PdfPCell(new Paragraph("\n\n" + digitTotal + " only\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			
			String strLines = "";
			for (int i = pertiSize; i < 33; i++) {
				strLines = strLines + "\n";
			}
//			System.out.println("pertiSize ===>> " +pertiSize + "strLines ===>> " +strLines);
			// New Row
			row1 = new PdfPCell(new Paragraph(strLines, small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(strLines, small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(strLines, small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			

			// New Row
			row1 = new PdfPCell(new Paragraph("TOTAL " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);


//			// New Row
//			row1 = new PdfPCell(new Paragraph(
////				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + "Chartered Accountants\n\n\n\n\n"
//				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + uF.showData(hmOrgInner.get("ORG_SUB_TITLE"), "") +"\n\n\n\n\n"
//				+ uF.showData(hmProjectOwnerDetails.get("EMP_NAME"), "") + "\n\n"
//				+ uF.showData(hmDesignation.get(hmProjectOwnerDetails.get("EMP_ID")), ""), smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(3);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("                     " + uF.showData(hmWlocation.get("WL_ECC1_NO"), "-")
//					+ "\nECC Code: ------------------------\n" + "                     " + uF.showData(hmWlocation.get("WL_ECC2_NO"), "-") + "\n\nPAN: "
////					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmWlocation.get("WL_REG_NO"), "") + "\n\n", smallBold));
//					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmOrgInner.get("ORG_REG_NO"), "") + "\n\n", smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(3);
//			table.addCell(row1);
//
//			// New Row
//			if (wLocation != null && wLocation.contains(",")) {
//				wLocation = wLocation.substring(0, wLocation.lastIndexOf(",")) + " and " + wLocation.substring(wLocation.lastIndexOf(",") + 1);
//			}
//			row1 = new PdfPCell(new Paragraph(wLocation, smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			row1.setFixedHeight(0f);
//			table.addCell(row1);

			// New Row
			List<Element> al7 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_7"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al7);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al8 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_8"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al8);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al9 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_9"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al9);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al10 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_10"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al10);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al11 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_11"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al11);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setFixedHeight(0f);
			table.addCell(row1);
			
			document.add(table);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}

	
	
	private void generateProjectAdHocPdfReport() {

		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
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

//			pst = con.prepareStatement("select * from org_details");
//			Map<String, Map<String, String>> hmOrg = new HashMap<String, Map<String, String>>();
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				Map<String, String> hmInner = new HashMap<String, String>();
//				hmInner.put("ORG_ID", rs.getString("org_id"));
//				hmInner.put("ORG_NAME", rs.getString("org_name"));
//				hmInner.put("ORG_LOGO", rs.getString("org_logo"));
//				hmInner.put("ORG_ADDRESS", rs.getString("org_address"));
//				hmInner.put("ORG_PINCODE", rs.getString("org_pincode"));
//				hmInner.put("ORG_CONTACT", rs.getString("org_contact1"));
//				hmInner.put("ORG_EMAIL", rs.getString("org_email"));
//				hmInner.put("ORG_STATE_ID", rs.getString("org_state_id"));
//				hmInner.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
//				hmInner.put("ORG_CITY", rs.getString("org_city"));
//				hmInner.put("ORG_CODE", rs.getString("org_code"));
//
//				hmOrg.put(rs.getString("org_id"), hmInner);
//			}
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
				hmInvoiceDetails.put("ADDRESS_ID", rs.getString("address_id"));
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
				hmInvoiceDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmInvoiceDetails.put("SERVICE_ID", rs.getString("service_id"));
				hmInvoiceDetails.put("CLIENT_NAME", uF.showData((String) hmAdhocClientDetails.get(rs.getString("client_id")), "N/A"));
				hmInvoiceDetails.put("CLIENT_TYPE", uF.showData((String) hmAdhocClientDetails.get(rs.getString("client_id") + "_TYPE"), "N/A"));

				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));

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
			// String client_name="";
			// String client_id="";
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				// client_id=rs.getString("client_id");
				// client_name=rs.getString("client_name");
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

			pst = con.prepareStatement("select * from client_address where client_address_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("ADDRESS_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(",", ",\n") : "";
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

			pst = con.prepareStatement("select * from deduction_tax_misc_details where ? between financial_year_from and financial_year_to and trail_status = 1");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			Map<String, String> hmTaxMiscSetting = new HashMap<String, String>();
			while (rs.next()) {
				hmTaxMiscSetting.put("DEDUCTION_TAX_MISC_ID", rs.getString("deduction_tax_misc_id"));
				hmTaxMiscSetting.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmTaxMiscSetting.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmTaxMiscSetting.put("FLAT_TDS", rs.getString("flat_tds"));
				hmTaxMiscSetting.put("SERVICE_TAX", rs.getString("service_tax"));
				hmTaxMiscSetting.put("DEDUCTION_TYPE", rs.getString("deduction_type"));
				hmTaxMiscSetting.put("FINANCIAL_YEAR_FROM", rs.getString("financial_year_from"));
				hmTaxMiscSetting.put("FINANCIAL_YEAR_TO", rs.getString("financial_year_to"));
				hmTaxMiscSetting.put("YEAR", rs.getString("_year"));
				hmTaxMiscSetting.put("ENTRY_TIMESTAMP", rs.getString("entry_timestamp"));
				hmTaxMiscSetting.put("USER_ID", rs.getString("user_id"));
				hmTaxMiscSetting.put("ACTIVITY", rs.getString("trail_status"));
				hmTaxMiscSetting.put("STATE_ID", rs.getString("state_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select bd1.bank_name,bd.branch_id,bd.branch_code,bd.bank_description,bd.bank_address,bd.bank_city,bd.bank_state_id,"
					+ "bd.bank_country_id,bd.bank_branch,bd.bank_email,bd.bank_fax,bd.bank_contact,bd.bank_ifsc_code,bd.bank_account_no,"
					+ " bd.bank_pincode,bd.bank_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id "
					+ " and bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("bank_name"));
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
				hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();

			ByteArrayOutputStream buffer = generateAdhocPdfDocument(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName, hmDesignation, 
					wLocation, client_address, outerAmtList, outerTaxList, hmTaxMiscSetting, client_poc, hmBankBranch, hmCurrencyDetails, hmWlocation,
					hmProjectDetails);

			if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {

				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=AdHoc_Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();

			} else {
				byte[] bytes = buffer.toByteArray();
				Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setSupervisor(true); // nF.setStrEmpId(sessionEmpId);
				nF.setStrEmailTo("");
				nF.setSupervisor(true);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("ProjectSummary.pdf");
				nF.sendNotifications();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void generateProjectAdHocPdfReportOtherCurr() {

		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
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

//			pst = con.prepareStatement("select * from org_details");
//			Map<String, Map<String, String>> hmOrg = new HashMap<String, Map<String, String>>();
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				Map<String, String> hmInner = new HashMap<String, String>();
//				hmInner.put("ORG_ID", rs.getString("org_id"));
//				hmInner.put("ORG_NAME", rs.getString("org_name"));
//				hmInner.put("ORG_LOGO", rs.getString("org_logo"));
//				hmInner.put("ORG_ADDRESS", rs.getString("org_address"));
//				hmInner.put("ORG_PINCODE", rs.getString("org_pincode"));
//				hmInner.put("ORG_CONTACT", rs.getString("org_contact1"));
//				hmInner.put("ORG_EMAIL", rs.getString("org_email"));
//				hmInner.put("ORG_STATE_ID", rs.getString("org_state_id"));
//				hmInner.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
//				hmInner.put("ORG_CITY", rs.getString("org_city"));
//				hmInner.put("ORG_CODE", rs.getString("org_code"));
//
//				hmOrg.put(rs.getString("org_id"), hmInner);
//			}
//			rs.close();
//			pst.close();

			pst = con.prepareStatement(" select * from promntc_invoice_details where  promntc_invoice_id=?");
			pst.setInt(1, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			String inrValue = null;
			while (rs.next()) {
				inrValue = getExchangeValue(uF, rs.getString("curr_id"));
				double dblOCInvoiceAmt = 0;
				double dblOCPartiTotAmt = 0;
				double dblOCOtherAmt = 0;
				
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
				hmInvoiceDetails.put("ADDRESS_ID", rs.getString("address_id"));
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
				hmInvoiceDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmInvoiceDetails.put("SERVICE_ID", rs.getString("service_id"));
				hmInvoiceDetails.put("CLIENT_NAME", uF.showData((String) hmAdhocClientDetails.get(rs.getString("client_id")), "N/A"));
				hmInvoiceDetails.put("CLIENT_TYPE", uF.showData((String) hmAdhocClientDetails.get(rs.getString("client_id") + "_TYPE"), "N/A"));

				hmInvoiceDetails.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmInvoiceDetails.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmInvoiceDetails.put("SERVICE_TAX", rs.getString("service_tax"));

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

			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? ");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmInvoiceAmtDetails = new LinkedHashMap<String, Map<String, String>>();
			List<List<String>> outerAmtList = new ArrayList<List<String>>();
			while (rs.next()) {
				double dblOCPartiAmt = 0;
								
				Map<String, String> hmInner = hmInvoiceAmtDetails.get(rs.getString("promntc_invoice_id"));
				if (hmInner == null)
					hmInner = new HashMap<String, String>();
				hmInner.put("PROJECT_INVOICE_AMT_ID", rs.getString("promntc_invoice_amt_id"));
				hmInner.put("INVOICE_PARTICULARS", rs.getString("invoice_particulars"));
				
//				dblOCPartiAmt = uF.parseToDouble(rs.getString("invoice_particulars_amount")) / uF.parseToDouble(inrValue);
				hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));

				hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
				hmInner.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));

				hmInvoiceAmtDetails.put(rs.getString("promntc_invoice_id"), hmInner);

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("promntc_invoice_amt_id"));
				innerList.add(rs.getString("invoice_particulars"));
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				innerList.add(rs.getString("promntc_invoice_id"));

				outerAmtList.add(innerList);

			}
			rs.close();
			pst.close();

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
			// String client_name="";
			// String client_id="";
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				// client_id=rs.getString("client_id");
				// client_name=rs.getString("client_name");
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

			pst = con.prepareStatement("select * from client_address where client_address_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("ADDRESS_ID")));
			rs = pst.executeQuery();
			String client_address = "";
			while (rs.next()) {
				client_address = rs.getString("client_address") != null ? rs.getString("client_address").replace(",", ",\n") : "";
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

			pst = con.prepareStatement("select * from deduction_tax_misc_details where ? between financial_year_from and financial_year_to and trail_status = 1");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			Map<String, String> hmTaxMiscSetting = new HashMap<String, String>();
			while (rs.next()) {
				hmTaxMiscSetting.put("DEDUCTION_TAX_MISC_ID", rs.getString("deduction_tax_misc_id"));
				hmTaxMiscSetting.put("STANDARD_TAX", rs.getString("standard_tax"));
				hmTaxMiscSetting.put("EDUCATION_TAX", rs.getString("education_tax"));
				hmTaxMiscSetting.put("FLAT_TDS", rs.getString("flat_tds"));
				hmTaxMiscSetting.put("SERVICE_TAX", rs.getString("service_tax"));
				hmTaxMiscSetting.put("DEDUCTION_TYPE", rs.getString("deduction_type"));
				hmTaxMiscSetting.put("FINANCIAL_YEAR_FROM", rs.getString("financial_year_from"));
				hmTaxMiscSetting.put("FINANCIAL_YEAR_TO", rs.getString("financial_year_to"));
				hmTaxMiscSetting.put("YEAR", rs.getString("_year"));
				hmTaxMiscSetting.put("ENTRY_TIMESTAMP", rs.getString("entry_timestamp"));
				hmTaxMiscSetting.put("USER_ID", rs.getString("user_id"));
				hmTaxMiscSetting.put("ACTIVITY", rs.getString("trail_status"));
				hmTaxMiscSetting.put("STATE_ID", rs.getString("state_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select bd1.bank_name,bd.branch_id,bd.branch_code,bd.bank_description,bd.bank_address,bd.bank_city,bd.bank_state_id,"
					+ "bd.bank_country_id,bd.bank_branch,bd.bank_email,bd.bank_fax,bd.bank_contact,bd.bank_ifsc_code,bd.bank_account_no,"
					+ " bd.bank_pincode,bd.bank_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id "
					+ " and bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("bank_name"));
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
				hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
			}
			rs.close();
			pst.close();

			ByteArrayOutputStream buffer = generateAdhocPdfDocumentOtherCurr(con, uF, hmInvoiceDetails, hmInvoiceAmtDetails, hmProjectOwnerDetails, hmEmpName,
					hmDesignation, wLocation, client_address, outerAmtList, hmTaxMiscSetting, client_poc, hmBankBranch, hmCurrencyDetails, hmWlocation,
					hmProjectDetails);

			if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {

				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=AdHoc_Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();

			} else {
				byte[] bytes = buffer.toByteArray();
				Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setSupervisor(true); // nF.setStrEmpId(sessionEmpId);
				nF.setStrEmailTo("");
				nF.setSupervisor(true);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("ProjectSummary.pdf");
				nF.sendNotifications();

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
			String client_address, List<List<String>> outerAmtList, List<List<String>> outerTaxList, Map<String, String> hmTaxMiscSetting, 
			String client_poc, Map<String, String> hmBankBranch, Map<String, Map<String, String>> hmCurrencyDetails,
			Map<String, String> hmWlocation, Map<String, String> hmProjectDetails) {

		String RESULT = "/home/user/Desktop/FormNo10.pdf";
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normal1 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font small1 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {

			// Map<String,String>
			// hmOrgInner=hmOrg.get(hmProjectOwnerDetails.get("EMP_ORG_ID"));

			Map<String, String> hmOrgInner = CF.getEmpOrgDetails(con, uF, hmProjectOwnerDetails.get("EMP_ID"));

			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null)
				hmCurr = new HashMap<String, String>();

			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);

			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			/*
			 * row1 = new PdfPCell(new Paragraph("", small));
			 * row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			 * row1.setPadding(2.5f); row1.setColspan(2); table.addCell(row1);
			 */

			row1 = new PdfPCell(new Paragraph("  Original  /  Cenvat Copy  /  Office Copy  ", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmOrgInner.get("ORG_NAME").toUpperCase().trim(), ""), normalwithbold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(6);
			table.addCell(row1);

			// New Row
//			row1 = new PdfPCell(new Paragraph("CHARTERED ACCOUNTANTS", normal1));
			row1 = new PdfPCell(new Paragraph(uF.showData(hmOrgInner.get("ORG_SUB_TITLE").toUpperCase().trim(), ""), normal1));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(6);
			table.addCell(row1);

			// New Row
			/*
			 * row1 = new PdfPCell(new
			 * Paragraph(uF.showData(hmOrgInner.get("ORG_ADDRESS"),
			 * ""),normal));
			 */
			String orgAddress = hmOrgInner.get("ORG_ADDRESS") != null ? hmOrgInner.get("ORG_ADDRESS").replace(",", ",\n") + "- "
					+ uF.showData(hmOrgInner.get("ORG_PINCODE"), "") : "";
			row1 = new PdfPCell(new Paragraph(orgAddress, small1));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(6);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("Tel   : " + uF.showData(hmWlocation.get("WL_CONTACT_NO"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("Fax   : " + uF.showData(hmWlocation.get("WL_FAX_NO"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("Email : " + uF.showData(hmProjectOwnerDetails.get("EMP_EMAIL"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("Bill No.: " + hmInvoiceDetails.get("INVOICE_CODE"), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			/*
			 * row1 = new PdfPCell(new Paragraph("Date:", small));
			 * row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * row1.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
			 * row1.setPadding(2.5f); table.addCell(row1);
			 */

			row1 = new PdfPCell(new Paragraph("Date  : " + uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			/*
			 * row1 = new PdfPCell(new
			 * Paragraph("  Original  /  Cenvat Copy  /  Office Copy  ", small));
			 * row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 * row1.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT );
			 * row1.setPadding(2.5f); row1.setColspan(2); table.addCell(row1);
			 */

			/*
			 * //New Row row1 = new PdfPCell(new
			 * Paragraph(client_poc+"\n"+uF.showData
			 * (hmInvoiceDetails.get("CLIENT_NAME").toUpperCase().trim(),
			 * "")+"\n" +client_address, smallBold)); //spoc name,client,address
			 * row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 * row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 * row1.setColspan(6); row1.setPadding(2.5f); table.addCell(row1);
			 */

			// New Row
			row1 = new PdfPCell(new Paragraph("\n" + uF.showData(hmInvoiceDetails.get("CLIENT_NAME").toUpperCase().trim(), "") + "\n\n" + client_address
					+ "\n\n", smallBold)); // spoc name,client,address
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			if(client_poc != null && !client_poc.trim().equals("")) {
				// New Row
				row1 = new PdfPCell(new Paragraph("Kind Attn : " + client_poc + "\n\n", smallBold)); // spoc name,client,address
				row1.setHorizontalAlignment(Element.ALIGN_CENTER);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			
			if (uF.parseToInt(hmInvoiceDetails.get("CLIENT_TYPE")) == 1 && hmProjectDetails.get("PRO_NAME") != null
					&& !hmProjectDetails.get("PRO_NAME").equals("")) {
				// New Row
				row1 = new PdfPCell(new Paragraph("Project Name : " + hmProjectDetails.get("PRO_NAME") + "\n\n", smallBold)); // project
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}

			// New Row
			row1 = new PdfPCell(new Paragraph("P A R T I C U L A R S", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			String currency = hmCurr.get("SHORT_CURR") != null && !hmCurr.get("SHORT_CURR").equals("") ? " (" + hmCurr.get("SHORT_CURR") + ")" : "";
			row1 = new PdfPCell(new Paragraph("AMOUNT " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("AMOUNT " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			// row1 = new PdfPCell(new
			// Paragraph("(Billing period taken as "+uF.showData(hmInvoiceDetails.get("INVOICE_FROM_DATE"),
			// "")+" - "+uF.showData(hmInvoiceDetails.get("INVOICE_TO_DATE"),
			// "")+")", small));
			// row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			// row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			// row1.setColspan(4);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("REFERENCE_NO_DESC"), "")+"\n\n", small));
			 row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 row1.setColspan(4);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);

			// New Row loop
			String sTax = "0";
			String eduCess = "0";
			String shsCess = "0";

			for (int i = 0; outerAmtList != null && i < outerAmtList.size(); i++) {
				List<String> innerList = outerAmtList.get(i);

				if (innerList.get(1) != null && innerList.get(1).trim().equals("STAX")) {
					sTax = uF.showData(innerList.get(2), "0");
				} else if (innerList.get(1) != null && innerList.get(1).trim().equals("EDUCESS")) {
					eduCess = uF.showData(innerList.get(2), "0");
				} else if (innerList.get(1) != null && innerList.get(1).trim().equals("SHSCESS")) {
					shsCess = uF.showData(innerList.get(2), "0");
				} else {
					if (uF.parseToDouble(innerList.get(2)) == 0.0) {
						continue;
					}
					row1 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
					row1.setHorizontalAlignment(Element.ALIGN_LEFT);
					row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					row1.setColspan(4);
					row1.setPadding(2.5f);
					table.addCell(row1);

					row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(innerList.get(2))), small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);

					row1 = new PdfPCell(new Paragraph("", small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);
				}
			}

			/*
			 * //New Row row1 = new PdfPCell(new
			 * Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_PARTICULAR"),
			 * ""), small)); row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 * row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 * row1.setColspan(4); row1.setPadding(2.5f); table.addCell(row1);
			 * 
			 * row1 = new PdfPCell(new
			 * Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_AMOUNT"),
			 * "0"), small)); row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * row1.setBorder(Rectangle.RIGHT); row1.setPadding(2.5f);
			 * table.addCell(row1);
			 * 
			 * row1 = new PdfPCell(new Paragraph("", small));
			 * row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * row1.setBorder(Rectangle.RIGHT); row1.setPadding(2.5f);
			 * table.addCell(row1);
			 */

			// New Row

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("" + uF.formatIntoComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))), small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			/*
			 * //New Row row1 = new PdfPCell(new
			 * Paragraph(uF.showData(hmProjectDetails.get("PRO_NAME"),
			 * "")+"\n"+uF.showData(hmProjectDetails.get("DESCRIPTION"),
			 * ""), small)); row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 * row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 * row1.setColspan(4); row1.setPadding(2.5f); table.addCell(row1);
			 * 
			 * row1 = new PdfPCell(new Paragraph("", small));
			 * row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * row1.setBorder(Rectangle.RIGHT); row1.setPadding(2.5f);
			 * table.addCell(row1);
			 * 
			 * row1 = new PdfPCell(new Paragraph("", small));
			 * row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * row1.setBorder(Rectangle.RIGHT); row1.setPadding(2.5f);
			 * table.addCell(row1);
			 */

			// //New Row
			// row1 = new PdfPCell(new
			// Paragraph(uF.showData(hmInvoiceDetails.get("PROJECT_DESCRIPTION"),
			// ""), small));
			// row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			// row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			// row1.setColspan(4);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("Add: Service Tax @ " + uF.showData(hmInvoiceDetails.get("SERVICE_TAX"), "0") + "%", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(sTax)), small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("Add: Educational Cess @ " + uF.showData(hmInvoiceDetails.get("EDUCATION_TAX"), "0") + "%", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(eduCess)), small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("Add: Secondary and Higher Secondary Cess @ " + uF.showData(hmInvoiceDetails.get("STANDARD_TAX"), "0") + "%",
					small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(shsCess)), small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// //New Row
			// row1 = new PdfPCell(new
			// Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"),
			// ""), small));
			// row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			// row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			// row1.setColspan(4);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);
			//
			// row1 = new PdfPCell(new Paragraph("", small));
			// row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// row1.setBorder(Rectangle.RIGHT);
			// row1.setPadding(2.5f);
			// table.addCell(row1);

			// New Row
			double totalAmt = uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"));
			String digitTotal = "";
			String strTotalAmt = "" + totalAmt;
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
				int totalAmt1 = (int) totalAmt;
				digitTotal = uF.digitsToWords(totalAmt1);
			}

			row1 = new PdfPCell(new Paragraph("\n\n" + digitTotal + " only\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("Tax is NOT required to be deducted on service tax portion\n\n", italicEffect));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), "") + "\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("TOTAL " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			String bankDetails = uF.showData(hmBankBranch.get("BRANCH_BANK_NAME"), "") + ", " + uF.showData(hmBankBranch.get("BRANCH_ADDRESS"), "") + ", "
					+ uF.showData(hmBankBranch.get("BRANCH_CITY"), "") + ",\nA/c No. " + uF.showData(hmBankBranch.get("BRANCH_ACCOUNT_NO"), "")
					+ ", IFSC code- " + uF.showData(hmBankBranch.get("BRANCH_IFSC_CODE") + "\n\n", "");
			row1 = new PdfPCell(new Paragraph("Bank Details: " + bankDetails, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(
//				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + "Chartered Accountants\n\n\n\n\n"
				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + uF.showData(hmOrgInner.get("ORG_SUB_TITLE"), "") +"\n\n\n\n\n"
				+ uF.showData(hmProjectOwnerDetails.get("EMP_NAME"), "") + "\n\n"
				+ uF.showData(hmDesignation.get(hmProjectOwnerDetails.get("EMP_ID")), ""), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("                     " + uF.showData(hmWlocation.get("WL_ECC1_NO"), "-")
					+ "\nECC Code: ------------------------\n" + "                     " + uF.showData(hmWlocation.get("WL_ECC2_NO"), "-") + "\n\nPAN: "
//					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmWlocation.get("WL_REG_NO"), "") + "\n\n", smallBold));
					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmOrgInner.get("ORG_REG_NO"), "") + "\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);

			/*
			 * row1 = new PdfPCell(new Paragraph("", smallBold));
			 * row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 * row1.setBorder(Rectangle.RIGHT); row1.setPadding(2.5f);
			 * table.addCell(row1);
			 */

			// New Row
			if (wLocation != null && wLocation.contains(",")) {
				wLocation = wLocation.substring(0, wLocation.lastIndexOf(",")) + " and " + wLocation.substring(wLocation.lastIndexOf(",") + 1);
			}
			row1 = new PdfPCell(new Paragraph(wLocation, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			document.add(table);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}
	
	
	
	private ByteArrayOutputStream generateAdhocPdfDocumentOtherCurr(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails, 
			Map<String, Map<String, String>> hmInvoiceAmtDetails, Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, 
			Map<String, String> hmDesignation, String wLocation, String client_address, List<List<String>> outerAmtList,
			Map<String, String> hmTaxMiscSetting, String client_poc, Map<String, String> hmBankBranch, Map<String, Map<String, String>> hmCurrencyDetails,
			Map<String, String> hmWlocation, Map<String, String> hmProjectDetails) {

		String RESULT = "/home/user/Desktop/FormNo10.pdf";
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normal1 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font small1 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {

			// Map<String,String>
			// hmOrgInner=hmOrg.get(hmProjectOwnerDetails.get("EMP_ORG_ID"));

			Map<String, String> hmOrgInner = CF.getEmpOrgDetails(con, uF, hmProjectOwnerDetails.get("EMP_ID"));

			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null)
				hmCurr = new HashMap<String, String>();

			String proCurrId = "3";
			if(uF.parseToInt(hmProjectDetails.get("PRO_CURR_ID")) > 0) {
				proCurrId = hmProjectDetails.get("PRO_CURR_ID");
			}
			Map<String, String> hmProCurr = hmCurrencyDetails.get(proCurrId);
			if (hmProCurr == null)
				hmProCurr = new HashMap<String, String>();
			
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);

			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("  Original  /  Cenvat Copy  /  Office Copy  ", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmOrgInner.get("ORG_NAME").toUpperCase().trim(), ""), normalwithbold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(6);
			table.addCell(row1);

			// New Row
//			row1 = new PdfPCell(new Paragraph("CHARTERED ACCOUNTANTS", normal1));
			row1 = new PdfPCell(new Paragraph(uF.showData(hmOrgInner.get("ORG_SUB_TITLE").toUpperCase().trim(), ""), normal1));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(6);
			table.addCell(row1);

			// New Row
			String orgAddress = hmOrgInner.get("ORG_ADDRESS") != null ? hmOrgInner.get("ORG_ADDRESS").replace(",", ",\n") + "- "
					+ uF.showData(hmOrgInner.get("ORG_PINCODE"), "") : "";
			row1 = new PdfPCell(new Paragraph(orgAddress, small1));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(6);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("Tel   : " + uF.showData(hmWlocation.get("WL_CONTACT_NO"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("Fax   : " + uF.showData(hmWlocation.get("WL_FAX_NO"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("Email : " + uF.showData(hmProjectOwnerDetails.get("EMP_EMAIL"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("Bill No.: " + hmInvoiceDetails.get("INVOICE_CODE"), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("Date  : " + uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("\n" + uF.showData(hmInvoiceDetails.get("CLIENT_NAME").toUpperCase().trim(), "") + "\n\n" + client_address
					+ "\n\n", smallBold)); // spoc name,client,address
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			if(client_poc != null && !client_poc.trim().equals("")) {
				// New Row
				row1 = new PdfPCell(new Paragraph("Kind Attn : " + client_poc + "\n\n", smallBold)); // spoc
																										// name,client,address
				row1.setHorizontalAlignment(Element.ALIGN_CENTER);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			
			if (uF.parseToInt(hmInvoiceDetails.get("CLIENT_TYPE")) == 1 && hmProjectDetails.get("PRO_NAME") != null
					&& !hmProjectDetails.get("PRO_NAME").equals("")) {
				// New Row
				row1 = new PdfPCell(new Paragraph("Project Name : " + hmProjectDetails.get("PRO_NAME") + "\n\n", smallBold)); // project
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(6);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}

			// New Row
			row1 = new PdfPCell(new Paragraph("P A R T I C U L A R S", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			String currency = hmCurr.get("LONG_CURR") != null && !hmCurr.get("LONG_CURR").equals("") ? " (" + hmCurr.get("LONG_CURR") + ")" : "";
			String proCurrency = hmProCurr.get("LONG_CURR") != null && !hmProCurr.get("LONG_CURR").equals("") ? " (" + hmProCurr.get("LONG_CURR") + ")" : "";
			row1 = new PdfPCell(new Paragraph(proCurrency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("REFERENCE_NO_DESC"), "")+"\n\n", small));
			 row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 row1.setColspan(4);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);

			// New Row loop
			String sTax = "0";
			String eduCess = "0";
			String shsCess = "0";

			for (int i = 0; outerAmtList != null && i < outerAmtList.size(); i++) {
				List<String> innerList = outerAmtList.get(i);

				if (innerList.get(1) != null && innerList.get(1).trim().equals("STAX")) {
					sTax = uF.showData(innerList.get(2), "0");
				} else if (innerList.get(1) != null && innerList.get(1).trim().equals("EDUCESS")) {
					eduCess = uF.showData(innerList.get(2), "0");
				} else if (innerList.get(1) != null && innerList.get(1).trim().equals("SHSCESS")) {
					shsCess = uF.showData(innerList.get(2), "0");
				} else {
					String partiAmount = "";
					String partiOCAmount = "";
					if (uF.parseToDouble(innerList.get(2)) > 0.0) {
						partiAmount = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2)));
						partiOCAmount = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3)));
					} else {
						partiAmount = "\n";
						partiOCAmount = "\n";
					}
					
					
					row1 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
					row1.setHorizontalAlignment(Element.ALIGN_LEFT);
					row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					row1.setColspan(4);
					row1.setPadding(2.5f);
					table.addCell(row1);

					row1 = new PdfPCell(new Paragraph(partiAmount, small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);

					row1 = new PdfPCell(new Paragraph(partiOCAmount, small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);
				}
			}

			//New Row
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// row1 = new PdfPCell(new
			// Paragraph(""+uF.formatIntoComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))), small));
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			// New Row
			String bankDetails = uF.showData(hmBankBranch.get("BRANCH_BANK_NAME"), "") + ", " + uF.showData(hmBankBranch.get("BRANCH_ADDRESS"), "") + ", "
			+ uF.showData(hmBankBranch.get("BRANCH_CITY"), "") 
			//+ ",\nA/c No. " + uF.showData(hmBankBranch.get("BRANCH_ACCOUNT_NO"), "")
			//+ ", IFSC code- " + uF.showData(hmBankBranch.get("BRANCH_IFSC_CODE") + "\n\n", "") 
			+ "\n Swift code: " + uF.showData(hmBankBranch.get("BRANCH_SWIFT_CODE"), "")
			+ "\n Bank Clearning code: " + uF.showData(hmBankBranch.get("BRANCH_BANK_CLEARING_CODE"), "") + "\n\n";
			row1 = new PdfPCell(new Paragraph("PLEASE PAY TO \n" + bankDetails, small)); // Bank Details
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmBankBranch.get("BRANCH_OTHER_INFO"), ""), small)); // Bank Other Details
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			double totalAmt = uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"));
			// System.out.println("totalAmt ===>>>> " + totalAmt);
			
			String digitTotal = "";
			String strTotalAmt = "" + uF.formatIntoTwoDecimalWithOutComma(totalAmt);
			// System.out.println("strTotalAmt ===>> " + strTotalAmt);
			
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
					digitTotal += " and" + uF.digitsToWords(pamt) + " cents";
				}
			} else {
				int totalAmt1 = (int) totalAmt;
				digitTotal = uF.digitsToWords(totalAmt1);
			}
			
			row1 = new PdfPCell(new Paragraph("\n\n" + digitTotal + " only\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			row1 = new PdfPCell(new Paragraph("TOTAL " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(
//				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + "Chartered Accountants\n\n\n\n\n"
				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + uF.showData(hmOrgInner.get("ORG_SUB_TITLE"), "") +"\n\n\n\n\n"
				+ uF.showData(hmProjectOwnerDetails.get("EMP_NAME"), "") + "\n\n"
				+ uF.showData(hmDesignation.get(hmProjectOwnerDetails.get("EMP_ID")), ""), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("                     " + uF.showData(hmWlocation.get("WL_ECC1_NO"), "-")
					+ "\nECC Code: ------------------------\n" + "                     " + uF.showData(hmWlocation.get("WL_ECC2_NO"), "-") + "\n\nPAN: "
//					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmWlocation.get("WL_REG_NO"), "") + "\n\n", smallBold));
					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmOrgInner.get("ORG_REG_NO"), "") + "\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			if (wLocation != null && wLocation.contains(",")) {
				wLocation = wLocation.substring(0, wLocation.lastIndexOf(",")) + " and " + wLocation.substring(wLocation.lastIndexOf(",") + 1);
			}
			row1 = new PdfPCell(new Paragraph(wLocation, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			table.addCell(row1);

			document.add(table);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}

	
	
	private void generateProjectPdfReport() {

		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
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
				hmInvoiceDetails.put("ADDRESS_ID", rs.getString("address_id"));
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
			

			
			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd  where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
			rs = pst.executeQuery();
//			String client_name = "";
			String client_id = "";
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				client_id = rs.getString("client_id");
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
			}
			rs.close();
			pst.close();

//			pst = con.prepareStatement("select * from deduction_tax_misc_details where ? between financial_year_from and financial_year_to and trail_status = 1");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			Map<String, String> hmTaxMiscSetting = new HashMap<String, String>();
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

//			pst = con.prepareStatement("select bd1.bank_name,bd.branch_id,bd.branch_code,bd.bank_description,bd.bank_address,bd.bank_city,bd.bank_state_id,"
//					+ "bd.bank_country_id,bd.bank_branch,bd.bank_email,bd.bank_fax,bd.bank_contact,bd.bank_ifsc_code,bd.bank_account_no,"
//					+ " bd.bank_pincode,bd.bank_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id "
//					+ " and bd.branch_id=? order by bd1.bank_name,bd.branch_code");
//			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
//			rs = pst.executeQuery();
//			Map<String, String> hmBankBranch = new HashMap<String, String>();
//			while (rs.next()) {
//				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("bank_name"));
//				hmBankBranch.put("BRANCH_ID", rs.getString("branch_id"));
//				hmBankBranch.put("BRANCH_CODE", rs.getString("branch_code"));
//				hmBankBranch.put("BRANCH_DESCRIPTION", rs.getString("bank_description"));
//				hmBankBranch.put("BRANCH_ADDRESS", rs.getString("bank_address"));
//				hmBankBranch.put("BRANCH_CITY", rs.getString("bank_city"));
//				hmBankBranch.put("BRANCH_STATE_ID", rs.getString("bank_state_id"));
//				hmBankBranch.put("BRANCH_COUNTRY_ID", rs.getString("bank_country_id"));
//				hmBankBranch.put("BRANCH_BRANCH", rs.getString("bank_branch"));
//				hmBankBranch.put("BRANCH_EMAIL", rs.getString("bank_email"));
//				hmBankBranch.put("BRANCH_FAX", rs.getString("bank_fax"));
//				hmBankBranch.put("BRANCH_CONTACT", rs.getString("bank_contact"));
//				hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
//				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
//				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
//				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
//			}
//			rs.close();
//			pst.close();

			InvoiceFormatwiseData invoiceFormatwiseData = new InvoiceFormatwiseData(request, session, CF, uF, con, getPro_id()+"");
			Map<String, String> hmInvoiceFormatData = invoiceFormatwiseData.getInvoiceFormatDataPDF(getInvoice_id());
//			System.out.println("hmInvoiceFormatData ===>>>> " + hmInvoiceFormatData);
			
			ByteArrayOutputStream buffer = generatePdfDocument(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName,
					hmDesignation, client_id, outerAmtList, outerTaxList, hmProjectDetails, hmCurrencyDetails, hmInvoiceFormatData);

			if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {

				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();

			} else {
				byte[] bytes = buffer.toByteArray();
				Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setSupervisor(true); // nF.setStrEmpId(sessionEmpId);
				nF.setStrEmailTo("");
				nF.setSupervisor(true);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("ProjectSummary.pdf");
				nF.sendNotifications();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private void generateProjectPdfReportOtherCurr() {

		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			System.out.println("12345 ===========>>> ");
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			// Map<String, String> hmEmpEmail = CF.getEmpEmailMap();

			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);

			pst = con.prepareStatement(" select * from promntc_invoice_details where pro_id = ? and promntc_invoice_id=?");
			pst.setInt(1, getPro_id());
			pst.setInt(2, uF.parseToInt(getInvoice_id()));
			rs = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			String inrValue = null;
			while (rs.next()) {

				inrValue = getExchangeValue(uF, rs.getString("curr_id"));
//				double dblOCInvoiceAmt = 0;
//				double dblOCPartiTotAmt = 0;
//				double dblOCOtherAmt = 0;

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
				hmInvoiceDetails.put("ADDRESS_ID", rs.getString("address_id"));
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

//			pst = con.prepareStatement(" select * from promntc_invoice_amt_details where promntc_invoice_id = ? ");
//			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_INVOICE_ID")));
//			rs = pst.executeQuery();
//			Map<String, Map<String, String>> hmInvoiceAmtDetails = new LinkedHashMap<String, Map<String, String>>();
//			List<List<String>> outerAmtList = new ArrayList<List<String>>();
//			while (rs.next()) {
//				double dblOCPartiAmt = 0;
//				Map<String, String> hmInner = hmInvoiceAmtDetails.get(rs.getString("promntc_invoice_id"));
//				if (hmInner == null)
//					hmInner = new HashMap<String, String>();
//				hmInner.put("PROJECT_INVOICE_AMT_ID", rs.getString("promntc_invoice_amt_id"));
//				hmInner.put("INVOICE_PARTICULARS", rs.getString("invoice_particulars"));
//
////				dblOCPartiAmt = uF.parseToDouble(rs.getString("invoice_particulars_amount")) / uF.parseToDouble(inrValue);
//				hmInner.put("OC_INVOICE_PARTICULARS_AMOUNT", rs.getString("oc_invoice_particulars_amount"));
//
//				hmInner.put("INVOICE_PARTICULARS_AMOUNT", rs.getString("invoice_particulars_amount"));
//				hmInner.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
//
//				hmInvoiceAmtDetails.put(rs.getString("promntc_invoice_id"), hmInner);
//
//				List<String> innerList = new ArrayList<String>();
//				innerList.add(rs.getString("promntc_invoice_amt_id"));
//				innerList.add(rs.getString("invoice_particulars"));
//				innerList.add(rs.getString("invoice_particulars_amount"));
//				innerList.add(rs.getString("oc_invoice_particulars_amount"));
//				innerList.add(rs.getString("promntc_invoice_id"));
//
//				outerAmtList.add(innerList);
//
//			}
//			rs.close();
//			pst.close();
			
			
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
			
			

			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd  where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("PROJECT_ID")));
			rs = pst.executeQuery();
			String client_id = "";
			Map<String, String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				client_id = rs.getString("client_id");

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

//			pst = con.prepareStatement("select * from deduction_tax_misc_details where ? between financial_year_from and financial_year_to and trail_status = 1");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			Map<String, String> hmTaxMiscSetting = new HashMap<String, String>();
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

			pst = con.prepareStatement("select bd1.bank_name,bd.branch_id,bd.branch_code,bd.bank_description,bd.bank_address,bd.bank_city," +
					"bd.bank_state_id,bd.bank_country_id,bd.bank_branch,bd.bank_email,bd.bank_fax,bd.bank_contact,bd.bank_ifsc_code," +
					"bd.bank_account_no,bd.bank_pincode,bd.bank_id,bd.swift_code,bd.bank_clearing_code,bd.other_information " +
					"from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id and bd.branch_id=? order by bd1.bank_name," +
					"bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
			rs = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rs.getString("bank_name"));
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
				hmBankBranch.put("BRANCH_IFSC_CODE", rs.getString("bank_ifsc_code"));
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rs.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rs.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rs.getString("bank_id"));
				hmBankBranch.put("BRANCH_SWIFT_CODE", rs.getString("swift_code"));
				hmBankBranch.put("BRANCH_BANK_CLEARING_CODE", rs.getString("bank_clearing_code"));
				hmBankBranch.put("BRANCH_OTHER_INFO", rs.getString("other_information"));
			}
			rs.close();
			pst.close();

			InvoiceFormatwiseData invoiceFormatwiseData = new InvoiceFormatwiseData(request, session, CF, uF, con, getPro_id()+"");
			Map<String, String> hmInvoiceFormatData = invoiceFormatwiseData.getInvoiceFormatDataPDF(getInvoice_id());
//			System.out.println("hmInvoiceFormatData ===>>>> " + hmInvoiceFormatData);
			
			ByteArrayOutputStream buffer = generatePdfDocumentOtherCurr(con, uF, hmInvoiceDetails, hmProjectOwnerDetails, hmEmpName,
					hmDesignation, client_id, outerAmtList, outerTaxList, hmProjectDetails, hmCurrencyDetails, hmInvoiceFormatData);
			
			if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {

				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_NO_" + hmInvoiceDetails.get("INVOICE_CODE") + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();

			} else {
				byte[] bytes = buffer.toByteArray();
				Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setSupervisor(true); // nF.setStrEmpId(sessionEmpId);
				nF.setStrEmailTo("");
				nF.setSupervisor(true);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("ProjectSummary.pdf");
				nF.sendNotifications();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public ByteArrayOutputStream generatePdfDocument(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails, 
		Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String client_id, 
		List<List<String>> outerAmtList, List<List<String>> outerTaxList, Map<String, String> hmProjectDetails, 
		Map<String, Map<String, String>> hmCurrencyDetails, Map<String, String> hmInvoiceFormatData) {
//System.out.println("generatePdfDocument =======");
		String RESULT = "/home/user/Desktop/FormNo10.pdf";
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normal1 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font small1 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {

//			Map<String, String> hmOrgInner = CF.getEmpOrgDetails(con, uF, hmProjectOwnerDetails.get("EMP_ID"));

			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null)
				hmCurr = new HashMap<String, String>();

			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);

			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("  Original  /  Cenvat Copy  /  Office Copy  ", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			
			String strelement = uF.showData(hmInvoiceFormatData.get("SECTION_1"), "");
			List<Element> al = HTMLWorker.parseToList(new StringReader(strelement), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al2 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_2"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			List<Element> al3 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_3"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al3);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al4 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_4"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al4);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al5 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_5"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al5);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			List<Element> al6 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_6"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al6);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
//			row1 = new PdfPCell(new Paragraph(uF.showData(hmOrgInner.get("ORG_NAME").toUpperCase().trim(), ""), normalwithbold));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(6);
//			table.addCell(row1);
//
//			// New Row
////			row1 = new PdfPCell(new Paragraph("CHARTERED ACCOUNTANTS", normal1));
//			row1 = new PdfPCell(new Paragraph(uF.showData(hmOrgInner.get("ORG_SUB_TITLE").toUpperCase().trim(), ""), normal1));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(6);
//			table.addCell(row1);
//
//			// New Row
//			String orgAddress = hmOrgInner.get("ORG_ADDRESS") != null ? hmOrgInner.get("ORG_ADDRESS").replace(",", ",\n") + "- "
//					+ uF.showData(hmOrgInner.get("ORG_PINCODE"), "") : "";
//			row1 = new PdfPCell(new Paragraph(orgAddress, small1));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(6);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("Tel   : " + uF.showData(hmWlocation.get("WL_CONTACT_NO"), ""), small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("Fax   : " + uF.showData(hmWlocation.get("WL_FAX_NO"), ""), small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(2);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("Email :" + uF.showData(hmProjectOwnerDetails.get("EMP_EMAIL"), ""), small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(2);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("Bill No.: " + hmInvoiceDetails.get("INVOICE_CODE"), small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("Date  : " + uF.showData(hmInvoiceDetails.get("INVOICE_DATE"), ""), small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
//			row1.setPadding(2.5f);
//			row1.setColspan(2);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("\n" + uF.showData(client_name.toUpperCase().trim(), "") + "\n" + client_address + "\n\n", smallBold)); // spoc
//																																						// name,client,address
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("Kind Attn : " + client_poc + "\n\n", smallBold)); // spoc
//																									// name,client,address
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("P A R T I C U L A R S", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			String currency = hmCurr.get("SHORT_CURR") != null && !hmCurr.get("SHORT_CURR").equals("") ? " (" + hmCurr.get("SHORT_CURR") + ")" : "";
			row1 = new PdfPCell(new Paragraph("AMOUNT " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("AMOUNT " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			int pertiSize = 0;
			pertiSize += (hmInvoiceDetails.get("REFERENCE_NO_DESC") != null && !hmInvoiceDetails.get("REFERENCE_NO_DESC").equals("")) ? (hmInvoiceDetails.get("REFERENCE_NO_DESC").length() / 60) : 0;
			
			 row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("REFERENCE_NO_DESC"), "")+"\n\n", small));
			 row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 row1.setColspan(4);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);

			 
			// New Row loop

				for (int i = 0; outerAmtList != null && i < outerAmtList.size(); i++) {
					List<String> innerList = outerAmtList.get(i);
					if (uF.parseToDouble(innerList.get(2)) == 0.0) {
						continue;
					}
					row1 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
					row1.setHorizontalAlignment(Element.ALIGN_LEFT);
					row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					row1.setColspan(4);
					row1.setPadding(2.5f);
					table.addCell(row1);

					row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))), small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);

					row1 = new PdfPCell(new Paragraph("", small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);
					
					pertiSize++;
				}
				
//			// New Row loop
//			String sTax = "0";
//			String eduCess = "0";
//			String shsCess = "0";
//
//			for (int i = 0; outerAmtList != null && i < outerAmtList.size(); i++) {
//				List<String> innerList = outerAmtList.get(i);
//
//				if (innerList.get(1) != null && innerList.get(1).trim().equals("STAX")) {
//					sTax = uF.showData(innerList.get(2), "0");
//				} else if (innerList.get(1) != null && innerList.get(1).trim().equals("EDUCESS")) {
//					eduCess = uF.showData(innerList.get(2), "0");
//				} else if (innerList.get(1) != null && innerList.get(1).trim().equals("SHSCESS")) {
//					shsCess = uF.showData(innerList.get(2), "0");
//				} else {
//					if (uF.parseToDouble(innerList.get(2)) == 0.0) {
//						continue;
//					}
//					row1 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
//					row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//					row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//					row1.setColspan(4);
//					row1.setPadding(2.5f);
//					table.addCell(row1);
//
//					row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(innerList.get(2))), small));
//					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//					row1.setBorder(Rectangle.RIGHT);
//					row1.setPadding(2.5f);
//					table.addCell(row1);
//
//					row1 = new PdfPCell(new Paragraph("", small));
//					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//					row1.setBorder(Rectangle.RIGHT);
//					row1.setPadding(2.5f);
//					table.addCell(row1);
//				}
//			}
//
//			// New Row
//			if (uF.parseToDouble(hmInvoiceDetails.get("OTHER_AMOUNT")) > 0.0) {
//
//				row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_PARTICULAR"), ""), small));
//				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//				row1.setColspan(4);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//
//				row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(hmInvoiceDetails.get("OTHER_AMOUNT"))), small));
//				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//				row1.setBorder(Rectangle.RIGHT);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//
//				row1 = new PdfPCell(new Paragraph("", small));
//				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//				row1.setBorder(Rectangle.RIGHT);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//			}

			// New Row

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("" + uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))), small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			
			// New Row
			for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
				List<String> innerList = outerTaxList.get(i);
				if (uF.parseToDouble(innerList.get(2)) == 0.0) {
					continue;
				}
//				row1 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), "")+" @ " + uF.showData(innerList.get(5), "0") + "%", small));
				row1 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(4);
				row1.setPadding(2.5f);
				table.addCell(row1);
	
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
	
				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				pertiSize++;
			}
			
//			// New Row
//			row1 = new PdfPCell(new Paragraph("Add: Service Tax @ " + uF.showData(hmInvoiceDetails.get("SERVICE_TAX"), "0") + "%", small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(sTax)), small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("Add: Educational Cess @ " + uF.showData(hmInvoiceDetails.get("EDUCATION_TAX"), "0") + "%", small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(eduCess)), small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph("Add: Secondary and Higher Secondary Cess @ " + uF.showData(hmInvoiceDetails.get("STANDARD_TAX"), "0") + "%",
//					small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(shsCess)), small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);

			// New Row
			double totalAmt = uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"));
			String digitTotal = "";
			String strTotalAmt = "" + totalAmt;
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
				int totalAmt1 = (int) totalAmt;
				digitTotal = uF.digitsToWords(totalAmt1);
			}

			row1 = new PdfPCell(new Paragraph("\n\n" + digitTotal + " only\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("Tax is NOT required to be deducted on service tax portion\n\n", italicEffect));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"), "") + "\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			row1 = new PdfPCell(new Paragraph("TOTAL " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

//			// New Row
//			String bankDetails = uF.showData(hmBankBranch.get("BRANCH_BANK_NAME"), "") + ", " + uF.showData(hmBankBranch.get("BRANCH_ADDRESS"), "") + ", "
//					+ uF.showData(hmBankBranch.get("BRANCH_CITY"), "") + ",\nA/c No. " + uF.showData(hmBankBranch.get("BRANCH_ACCOUNT_NO"), "")
//					+ ", IFSC code- " + uF.showData(hmBankBranch.get("BRANCH_IFSC_CODE") + "\n\n", "");
//			row1 = new PdfPCell(new Paragraph("Bank Details: " + bankDetails, smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph(
////				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + "Chartered Accountants\n\n\n\n\n"
//				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + uF.showData(hmOrgInner.get("ORG_SUB_TITLE"), "") +"\n\n\n\n\n"
//				+ uF.showData(hmProjectOwnerDetails.get("EMP_NAME"), "")+"\n\n"+uF.showData(hmDesignation.get(hmProjectOwnerDetails.get("EMP_ID")), ""), smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(3);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("                     " + uF.showData(hmWlocation.get("WL_ECC1_NO"), "-")
//					+ "\nECC Code: ------------------------\n" + "                     " + uF.showData(hmWlocation.get("WL_ECC2_NO"), "-") + "\n\nPAN: "
////					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmWlocation.get("WL_REG_NO"), "") + "\n\n", smallBold));
//					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmOrgInner.get("ORG_REG_NO"), "") + "\n\n", smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(3);
//			table.addCell(row1);
//
//			// New Row
//			if (wLocation != null && wLocation.contains(",")) {
//				wLocation = wLocation.substring(0, wLocation.lastIndexOf(",")) + " and " + wLocation.substring(wLocation.lastIndexOf(",") + 1);
//			}
//			row1 = new PdfPCell(new Paragraph(wLocation, smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			row1.setFixedHeight(0f);
//			table.addCell(row1);

			// New Row
			List<Element> al7 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_7"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al7);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al8 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_8"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al8);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al9 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_9"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al9);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al10 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_10"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al10);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al11 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_11"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al11);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setFixedHeight(0f);
			table.addCell(row1);
			
			document.add(table);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}
//	con, uF, hmInvoiceDetails, hmInvoiceAmtDetails, hmProjectOwnerDetails, hmEmpName,
//	hmDesignation, client_id, outerAmtList, hmProjectDetails, hmTaxMiscSetting, hmCurrencyDetails, 
	
	public ByteArrayOutputStream generatePdfDocumentOtherCurr(Connection con, UtilityFunctions uF, Map<String, String> hmInvoiceDetails, 
		Map<String, String> hmProjectOwnerDetails, Map<String, String> hmEmpName, Map<String, String> hmDesignation, String client_id, 
		List<List<String>> outerAmtList, List<List<String>> outerTaxList, Map<String, String> hmProjectDetails, 
		Map<String, Map<String, String>> hmCurrencyDetails, Map<String, String> hmInvoiceFormatData) {

		String RESULT = "/home/user/Desktop/FormNo10.pdf";
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normal1 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font small1 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try { 

//			Map<String, String> hmOrgInner = CF.getEmpOrgDetails(con, uF, hmProjectOwnerDetails.get("EMP_ID"));

			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null)
				hmCurr = new HashMap<String, String>();

			String proCurrId = "3";
			if(uF.parseToInt(hmProjectDetails.get("PRO_CURR_ID")) > 0) {
				proCurrId = hmProjectDetails.get("PRO_CURR_ID");
			}
			Map<String, String> hmProCurr = hmCurrencyDetails.get(proCurrId);
			if (hmProCurr == null)
				hmProCurr = new HashMap<String, String>();
			
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			table.setFooterRows(25);

			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("  Original  /  Cenvat Copy  /  Office Copy  ", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row
			String strelement = uF.showData(hmInvoiceFormatData.get("SECTION_1"), "");
			List<Element> al = HTMLWorker.parseToList(new StringReader(strelement), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al2 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_2"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			List<Element> al3 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_3"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al3);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al4 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_4"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al4);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al5 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_5"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al5);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			List<Element> al6 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_6"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al6);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			row1 = new PdfPCell(new Paragraph("P A R T I C U L A R S", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);
			String currency = hmCurr.get("LONG_CURR") != null && !hmCurr.get("LONG_CURR").equals("") ? " (" + hmCurr.get("LONG_CURR") + ")" : "";
			String proCurrency = hmProCurr.get("LONG_CURR") != null && !hmProCurr.get("LONG_CURR").equals("") ? " (" + hmProCurr.get("LONG_CURR") + ")" : "";
			row1 = new PdfPCell(new Paragraph(proCurrency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			int pertiSize = 0;
			pertiSize += (hmInvoiceDetails.get("REFERENCE_NO_DESC") != null && !hmInvoiceDetails.get("REFERENCE_NO_DESC").equals("")) ? (hmInvoiceDetails.get("REFERENCE_NO_DESC").length() / 60) : 0;
			
			row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("REFERENCE_NO_DESC"), "")+"\n\n", small));
			 row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 row1.setColspan(4);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);

			 
			// New Row loop
				for (int i = 0; outerAmtList != null && i < outerAmtList.size(); i++) {
					List<String> innerList = outerAmtList.get(i);
					String partiAmount = "";
					String partiOCAmount = "";
					if (uF.parseToDouble(innerList.get(2)) > 0.0) {
						partiAmount = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2)));
						partiOCAmount = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3)));
					} else {
						partiAmount = "\n";
						partiOCAmount = "\n";
					}
					
					row1 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
					row1.setHorizontalAlignment(Element.ALIGN_LEFT);
					row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					row1.setColspan(4);
					row1.setPadding(2.5f);
					table.addCell(row1);

					row1 = new PdfPCell(new Paragraph(partiAmount, small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);

					row1 = new PdfPCell(new Paragraph(partiOCAmount, small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);
					
					pertiSize++;
				}
				
			 
//			// New Row loop
//			String sTax = "0";
//			String eduCess = "0";
//			String shsCess = "0";
//
//			for (int i = 0; outerAmtList != null && i < outerAmtList.size(); i++) {
//				List<String> innerList = outerAmtList.get(i);
//
//				if (innerList.get(1) != null && innerList.get(1).trim().equals("STAX")) {
//					sTax = uF.showData(innerList.get(2), "0");
//				} else if (innerList.get(1) != null && innerList.get(1).trim().equals("EDUCESS")) {
//					eduCess = uF.showData(innerList.get(2), "0");
//				} else if (innerList.get(1) != null && innerList.get(1).trim().equals("SHSCESS")) {
//					shsCess = uF.showData(innerList.get(2), "0");
//				} else {
//					String partiAmount = "";
//					String partiOCAmount = "";
//					if (uF.parseToDouble(innerList.get(2)) > 0.0) {
//						partiAmount = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2)));
//						partiOCAmount = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3)));
//					} else {
//						partiAmount = "\n";
//						partiOCAmount = "\n";
//					}
//					
//					row1 = new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
//					row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//					row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//					row1.setColspan(4);
//					row1.setPadding(2.5f);
//					table.addCell(row1);
//
//					row1 = new PdfPCell(new Paragraph(partiAmount, small));
//					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//					row1.setBorder(Rectangle.RIGHT);
//					row1.setPadding(2.5f);
//					table.addCell(row1);
//
//					row1 = new PdfPCell(new Paragraph(partiOCAmount, small));
//					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//					row1.setBorder(Rectangle.RIGHT);
//					row1.setPadding(2.5f);
//					table.addCell(row1);
//				}
//			}
//
//			// New Row
//			if (uF.parseToDouble(hmInvoiceDetails.get("OTHER_AMOUNT")) > 0.0) {
//
//				row1 = new PdfPCell(new Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_PARTICULAR"), ""), small));
//				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//				row1.setColspan(4);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//
//				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OTHER_AMOUNT"))), small));
//				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//				row1.setBorder(Rectangle.RIGHT);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//
//				row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_OTHER_AMOUNT"))), small));
//				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//				row1.setBorder(Rectangle.RIGHT);
//				row1.setPadding(2.5f);
//				table.addCell(row1);
//			}

				// New Row

				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(4);
				row1.setPadding(2.5f);
				table.addCell(row1);

				row1 = new PdfPCell(new Paragraph(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("PARTICULARS_TOTAL_AMOUNT"))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
//				System.out.println("OC_PARTICULARS_TOTAL_AMOUNT =======>> " + hmInvoiceDetails.get("OC_PARTICULARS_TOTAL_AMOUNT"));
				row1 = new PdfPCell(new Paragraph(""+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_PARTICULARS_TOTAL_AMOUNT"))), small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
				row1.setPadding(2.5f);
				table.addCell(row1);

				
				// New Row
				for (int i = 0; outerTaxList != null && i < outerTaxList.size(); i++) {
					List<String> innerList = outerTaxList.get(i);
					if (uF.parseToDouble(innerList.get(2)) == 0.0) {
						continue;
					}
					row1 = new PdfPCell(new Paragraph("Add: "+uF.showData(innerList.get(1), "")+" @ " + uF.showData(innerList.get(6), "0") + "%", small));
					row1.setHorizontalAlignment(Element.ALIGN_LEFT);
					row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					row1.setColspan(4);
					row1.setPadding(2.5f);
					table.addCell(row1);
		
					row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(2))), small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);
		
					row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(innerList.get(3))), small));
					row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					row1.setBorder(Rectangle.RIGHT);
					row1.setPadding(2.5f);
					table.addCell(row1);
					
					pertiSize++;
				}
			
				
//			// New Row
//			String bankDetails = uF.showData(hmBankBranch.get("BRANCH_BANK_NAME"), "") + ", " + uF.showData(hmBankBranch.get("BRANCH_ADDRESS"), "") + ", "
//			+ uF.showData(hmBankBranch.get("BRANCH_CITY"), "") 
////			+ ",\nA/c No. " + uF.showData(hmBankBranch.get("BRANCH_ACCOUNT_NO"), "")
////			+ ", IFSC code- " + uF.showData(hmBankBranch.get("BRANCH_IFSC_CODE") + "\n\n", "") 
//			+ "\n Swift code: " + uF.showData(hmBankBranch.get("BRANCH_SWIFT_CODE"), "")
//			+ "\n Bank Clearning code: " + uF.showData(hmBankBranch.get("BRANCH_BANK_CLEARING_CODE"), "") + "\n\n";
//			row1 = new PdfPCell(new Paragraph("PLEASE PAY TO \n" + bankDetails, small)); // Bank Details
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			// New Row
//			row1 = new PdfPCell(new Paragraph(uF.showData(hmBankBranch.get("BRANCH_OTHER_INFO"), ""), small)); // Bank Other Details
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(4);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			table.addCell(row1);

			// New Row
			double totalAmt = uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"));
			// System.out.println("totalAmt ===>>>> " + totalAmt);

			String digitTotal = "";
			String strTotalAmt = "" + uF.formatIntoTwoDecimalWithOutComma(totalAmt);
			// System.out.println("strTotalAmt ===>> " + strTotalAmt);

			if (strTotalAmt.contains(".")) {
				// System.out.println("strTotalAmt 1 ===>> " + strTotalAmt);
				strTotalAmt = strTotalAmt.replace(".", ",");
				// System.out.println("strTotalAmt 2 ===>> " + strTotalAmt);
				String[] temp = strTotalAmt.split(",");
				digitTotal = uF.digitsToWords(uF.parseToInt(temp[0]));
				if (uF.parseToInt(temp[1]) > 0) {
					int pamt = 0;
					if (temp[1].length() == 1) {
						pamt = uF.parseToInt(temp[1] + "0");
					} else {
						pamt = uF.parseToInt(temp[1]);
					}
					digitTotal += " and" + uF.digitsToWords(pamt) + " cents";
				}
			} else {
				int totalAmt1 = (int) totalAmt;
				digitTotal = uF.digitsToWords(totalAmt1);
			}

			row1 = new PdfPCell(new Paragraph("\n\n" + digitTotal + " only\n\n", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			
			//New Row
			 row1 = new PdfPCell(new
			 Paragraph("Tax is NOT required to be deducted on service tax portion\n\n",
			 italicEffect));
			 row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 row1.setColspan(4);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 //New Row
			 row1 = new PdfPCell(new
			 Paragraph(uF.showData(hmInvoiceDetails.get("OTHER_DESCRIPTION"),
			 "")+"\n\n", smallBold));
			 row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			 row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			 row1.setColspan(4);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);
			
			 row1 = new PdfPCell(new Paragraph("", small));
			 row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 row1.setBorder(Rectangle.RIGHT);
			 row1.setPadding(2.5f);
			 table.addCell(row1);

			 
			String strLines = "";
			for (int i = pertiSize; i < 23; i++) {
				strLines = strLines + "\n";
			}
			// New Row
			row1 = new PdfPCell(new Paragraph(strLines, small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(strLines, small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(strLines, small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);
			

			// New Row
			row1 = new PdfPCell(new Paragraph("TOTAL " + currency, smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceDetails.get("OC_INVOICE_AMOUNT"))), smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
//			String bankDetails = uF.showData(hmBankBranch.get("BRANCH_BANK_NAME"), "") + ", " + uF.showData(hmBankBranch.get("BRANCH_ADDRESS"), "") + ", "
//					+ uF.showData(hmBankBranch.get("BRANCH_CITY"), "") + ",\nA/c No. " + uF.showData(hmBankBranch.get("BRANCH_ACCOUNT_NO"), "")
//					+ ", IFSC code- " + uF.showData(hmBankBranch.get("BRANCH_IFSC_CODE") + "\n\n", "");
//			row1 = new PdfPCell(new Paragraph("Bank Details: " + bankDetails, smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			table.addCell(row1);

//			// New Row
//			row1 = new PdfPCell(new Paragraph(
////					"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + "Chartered Accountants\n\n\n\n\n"
//				"For " + uF.showData(hmOrgInner.get("ORG_NAME"), "") + "\n" + uF.showData(hmOrgInner.get("ORG_SUB_TITLE"), "") +"\n\n\n\n\n"
//				+ uF.showData(hmProjectOwnerDetails.get("EMP_NAME"), "") + "\n\n"
//				+ uF.showData(hmDesignation.get(hmProjectOwnerDetails.get("EMP_ID")), ""), smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(3);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			row1 = new PdfPCell(new Paragraph("                     " + uF.showData(hmWlocation.get("WL_ECC1_NO"), "-")
//					+ "\nECC Code: ------------------------\n" + "                     " + uF.showData(hmWlocation.get("WL_ECC2_NO"), "-") + "\n\nPAN: "
////					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmWlocation.get("WL_REG_NO"), "") + "\n\n", smallBold));
//					+ uF.showData(hmOrgInner.get("ORG_PAN_NO"), "") + "\n\nREGN. NO.: " + uF.showData(hmOrgInner.get("ORG_REG_NO"), "") + "\n\n", smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setPadding(2.5f);
//			row1.setColspan(3);
//			table.addCell(row1);
//
//			// New Row
//			if (wLocation != null && wLocation.contains(",")) {
//				wLocation = wLocation.substring(0, wLocation.lastIndexOf(",")) + " and " + wLocation.substring(wLocation.lastIndexOf(",") + 1);
//			}
//			row1 = new PdfPCell(new Paragraph(wLocation, smallBold));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			row1.setFixedHeight(0f);
//			table.addCell(row1);

			// New Row
			List<Element> al7 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_7"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al7);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al8 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_8"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al8);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al9 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_9"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al9);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al10 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_10"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al10);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al11 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceFormatData.get("SECTION_11"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al11);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setFixedHeight(0f);
			table.addCell(row1);
			
			
			document.add(table);

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
			// List<String> exchangeCurrList = new ArrayList<String>();
			pst = con.prepareStatement("select short_currency,long_currency,inr_value,updated_by,update_date from currency_details where currency_id = ?");
			pst.setInt(1, uF.parseToInt(currencyType));
			rs = pst.executeQuery();
			while (rs.next()) {
				// exchangeCurrList.add(uF.showData(rs.getString("short_currency"),
				// ""));
				// exchangeCurrList.add(uF.showData(rs.getString("long_currency"),
				// ""));
				inrValue = uF.showData(rs.getString("inr_value"), "");
				// exchangeCurrList.add(uF.showData(rs.getString("inr_value"),
				// ""));
				// exchangeCurrList.add(uF.showData(CF.getEmpNameMapByEmpId(con,
				// rs.getString("updated_by")), ""));
				// exchangeCurrList.add(uF.showData(uF.getDateFormat(rs.getString("update_date"),
				// DBDATE, CF.getStrReportDateFormat()), ""));
			}
			rs.close();
			pst.close();

			// request.setAttribute("exchangeCurrList", exchangeCurrList);

		} catch (Exception e) {
			// request.setAttribute("STATUS_MSG",
			// "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return inrValue;
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

}
