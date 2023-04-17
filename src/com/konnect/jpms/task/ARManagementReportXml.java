package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ARManagementReportXml implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	HttpSession session;
	String sessionEmpId;
	CommonFunctions CF;

	String[] f_service; 
	String[] f_client;
	
	String strStartDate;
	String strEndDate;
	String generate;
	String fromPage;
	
	public void execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;

		sessionEmpId = (String) session.getAttribute(EMPID);

		UtilityFunctions uF = new UtilityFunctions();
		if(getGenerate() != null) {
			if(getFromPage() != null && getFromPage().equals("sbu")) {
				viewSBUWiseReport(uF);
			} else {
				viewClientWiseReport(uF);
			}
		}
		// return null;
	}

	
	private void viewClientWiseReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery1=new StringBuilder();
			sbQuery1.append("select a.*,p.service,p.level_id from (select pbad.*, pid.invoice_code,pid.other_amount,pid.invoice_generated_date from promntc_bill_amt_details pbad," +
				" promntc_invoice_details pid where pbad.invoice_id=pid.promntc_invoice_id and pid.is_cancel=false ) a, projectmntnc p " +
				" where a.pro_id = p.pro_id and p.pro_id>0 ");
			if(getF_client() != null && getF_client().length>0) {
				sbQuery1.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery1.append(" and to_date(a.invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery1.append(" order by a.bill_id desc ");
			
			pst=con.prepareStatement(sbQuery1.toString());
//			System.out.println("pst======>"+pst);
			rs=pst.executeQuery();
//			List<Map<String, String>> invoiceBillList = new ArrayList<Map<String,String>>();
			Map<String, Map<String, String>> hmInvoiceBillData = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmBillReceiveDate = new HashMap<String, String>();
			
			while(rs.next()) {
				Map<String, String> hmInvoice = new HashMap<String, String>();
				double dblReceivedAmt = uF.parseToDouble(hmInvoice.get(rs.getString("invoice_id")+"_BILL_RECEIVED_AMOUNT")+"");
				dblReceivedAmt += rs.getDouble("received_amount");
				double dbltdsAmt = uF.parseToDouble(hmInvoice.get(rs.getString("invoice_id")+"_BILL_RECEIVED_AMOUNT")+"");
				dbltdsAmt += rs.getDouble("tds_deducted");
				double dblprevYearTdsAmt = uF.parseToDouble(hmInvoice.get(rs.getString("invoice_id")+"_BILL_RECEIVED_AMOUNT")+"");
				dblprevYearTdsAmt += rs.getDouble("prev_year_tds_deducted");
//				hmInvoice.put("PROJECT_INVOICE_ID", rs.getString("invoice_id"));
				hmInvoice.put(rs.getString("invoice_id")+"_BILL_RECEIVED_AMOUNT", dblReceivedAmt+"");				
				hmInvoice.put(rs.getString("invoice_id")+"_BILL_TDS_DEDUCTED", dbltdsAmt+"");
				hmInvoice.put(rs.getString("invoice_id")+"_BILL_PREV_YEAR_TDS_DEDUCTED", dblprevYearTdsAmt+"");
//				hmInvoice.put(rs.getString("invoice_id")+"_BILL_BALANCE_AMOUNT", rs.getDouble("balance_amount"));
				hmBillReceiveDate.put(rs.getString("invoice_id"), uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				hmInvoiceBillData.put(rs.getString("invoice_id"), hmInvoice);
			} 
			rs.close();
			pst.close();
			
//			System.out.println("hmInvoiceBillData ======>>> " + hmInvoiceBillData);
			
			
			StringBuilder sbQuery2=new StringBuilder();
			sbQuery2.append("select piad.invoice_particulars,piad.promntc_invoice_id from promntc_invoice_details pid,promntc_invoice_amt_details piad, projectmntnc p where " +
				" pid.is_cancel=false and pid.promntc_invoice_id = piad.promntc_invoice_id and pid.pro_id = p.pro_id and pid.pro_id>0 and piad.invoice_particulars != 'STAX' and piad.invoice_particulars != 'EDUCESS' and piad.invoice_particulars != 'SHSCESS' ");
			if(getF_client() != null && getF_client().length>0) {
				sbQuery2.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery2.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery2.append(" order by pid.promntc_invoice_id desc ");
			
			pst=con.prepareStatement(sbQuery2.toString());
			Map<String, String> hmInvoicePerticulars = new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()) {
				StringBuilder sbPertis = new StringBuilder();
				String pertis = hmInvoicePerticulars.get(rs.getString("promntc_invoice_id"));
				if(pertis == null || pertis.equals("")) {
					sbPertis = new StringBuilder();
					sbPertis.append(rs.getString("invoice_particulars"));
				} else {
					sbPertis.append(hmInvoicePerticulars.get(rs.getString("promntc_invoice_id")));
					sbPertis.append(", "+rs.getString("invoice_particulars"));
				}
				hmInvoicePerticulars.put(rs.getString("promntc_invoice_id"), sbPertis.toString());
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select pid.*,p.service,p.level_id,p.sbu_id from promntc_invoice_details pid, projectmntnc p where " +
				" pid.is_cancel=false and pid.pro_id = p.pro_id and pid.pro_id>0 ");
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" order by pid.promntc_invoice_id desc ");
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmInvoiceData = new LinkedHashMap<String, Map<String, String>>();
//			List<Map<String, String>> invoiceBillList = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				String days = uF.dateDifference(rs.getString("invoice_generated_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				Map<String, String> hmInvoice = new LinkedHashMap<String, String>();
				Map<String, String> hmInvoiceBill = hmInvoiceBillData.get(rs.getString("promntc_invoice_id"));
				if(hmInvoiceBill == null) hmInvoiceBill = new HashMap<String, String>();
				
				double tdsBooked = uF.parseToDouble(hmInvoiceBill.get(rs.getString("promntc_invoice_id")+"_BILL_TDS_DEDUCTED")) + uF.parseToDouble(hmInvoiceBill.get(rs.getString("promntc_invoice_id")+"_BILL_PREV_YEAR_TDS_DEDUCTED"));
				double outstandingAmt = uF.parseToDouble(rs.getString("invoice_amount")) - (uF.parseToDouble(hmInvoiceBill.get(rs.getString("promntc_invoice_id")+"_BILL_RECEIVED_AMOUNT")) + tdsBooked);
				hmInvoice.put("sbuname", CF.getServiceNameById(con, rs.getString("sbu_id")));
				hmInvoice.put("invoiceno", rs.getString("invoice_code"));
				hmInvoice.put("particulars", uF.showData(hmInvoicePerticulars.get(rs.getString("promntc_invoice_id")), "-"));
				hmInvoice.put("date", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("invoiceamount", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("invoice_amount"))));
				hmInvoice.put("invoiceamountreceived", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceBill.get(rs.getString("promntc_invoice_id")+"_BILL_RECEIVED_AMOUNT"))));
				hmInvoice.put("tdsbooked",  uF.formatIntoTwoDecimalWithOutComma(tdsBooked));
				hmInvoice.put("outstandingamount", uF.formatIntoTwoDecimalWithOutComma(outstandingAmt));
				hmInvoice.put("receiptdate", uF.showData(hmBillReceiveDate.get(rs.getString("promntc_invoice_id")),"-"));
				if(outstandingAmt > 0) {
					hmInvoice.put("overduebydays", days+" days");
				} else {
					hmInvoice.put("overduebydays", "-");
				}
				hmInvoiceData.put(rs.getString("promntc_invoice_id"), hmInvoice);
//				invoiceBillList.add(hmInvoice);
			} 
			rs.close();
			pst.close();
//			System.out.println("hmInvoiceData ===>> " + hmInvoiceData);
			
			createXML("client", hmInvoiceData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void viewSBUWiseReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery1=new StringBuilder();
			sbQuery1.append("select a.*,p.service,p.level_id from (select pbad.*, pid.invoice_code,pid.other_amount,pid.invoice_generated_date from promntc_bill_amt_details pbad," +
				" promntc_invoice_details pid where pbad.invoice_id=pid.promntc_invoice_id and pid.is_cancel=false ) a, projectmntnc p " +
				" where a.pro_id = p.pro_id and p.pro_id>0 ");
			if(getF_service() != null && getF_service().length>0) {
				sbQuery1.append(" and sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery1.append(" and to_date(a.invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery1.append(" order by a.bill_id desc ");
			
			pst=con.prepareStatement(sbQuery1.toString());
//			System.out.println("pst======>"+pst);
			rs=pst.executeQuery();
//			List<Map<String, String>> invoiceBillList = new ArrayList<Map<String,String>>();
			Map<String, Map<String, String>> hmInvoiceBillData = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmBillReceiveDate = new HashMap<String, String>();
			
			while(rs.next()) {
				Map<String, String> hmInvoice = new HashMap<String, String>();
				double dblReceivedAmt = uF.parseToDouble(hmInvoice.get(rs.getString("invoice_id")+"_BILL_RECEIVED_AMOUNT")+"");
				dblReceivedAmt += rs.getDouble("received_amount");
				double dbltdsAmt = uF.parseToDouble(hmInvoice.get(rs.getString("invoice_id")+"_BILL_RECEIVED_AMOUNT")+"");
				dbltdsAmt += rs.getDouble("tds_deducted");
				double dblprevYearTdsAmt = uF.parseToDouble(hmInvoice.get(rs.getString("invoice_id")+"_BILL_RECEIVED_AMOUNT")+"");
				dblprevYearTdsAmt += rs.getDouble("prev_year_tds_deducted");
//				hmInvoice.put("PROJECT_INVOICE_ID", rs.getString("invoice_id"));
				hmInvoice.put(rs.getString("invoice_id")+"_BILL_RECEIVED_AMOUNT", dblReceivedAmt+"");				
				hmInvoice.put(rs.getString("invoice_id")+"_BILL_TDS_DEDUCTED", dbltdsAmt+"");
				hmInvoice.put(rs.getString("invoice_id")+"_BILL_PREV_YEAR_TDS_DEDUCTED", dblprevYearTdsAmt+"");
//				hmInvoice.put(rs.getString("invoice_id")+"_BILL_BALANCE_AMOUNT", rs.getDouble("balance_amount"));
				hmBillReceiveDate.put(rs.getString("invoice_id"), uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				hmInvoiceBillData.put(rs.getString("invoice_id"), hmInvoice);
			} 
			rs.close();
			pst.close();
			
//			System.out.println("hmInvoiceBillData ======>>> " + hmInvoiceBillData);
			
			
			StringBuilder sbQuery2=new StringBuilder();
			sbQuery2.append("select piad.invoice_particulars,piad.promntc_invoice_id from promntc_invoice_details pid,promntc_invoice_amt_details piad, projectmntnc p where " +
				" pid.is_cancel=false and pid.promntc_invoice_id = piad.promntc_invoice_id and pid.pro_id = p.pro_id and pid.pro_id>0 and piad.invoice_particulars != 'STAX' and piad.invoice_particulars != 'EDUCESS' and piad.invoice_particulars != 'SHSCESS' ");
			if(getF_client() != null && getF_client().length>0) {
				sbQuery2.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery2.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery2.append(" order by pid.promntc_invoice_id desc ");
			
			pst=con.prepareStatement(sbQuery2.toString());
			Map<String, String> hmInvoicePerticulars = new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()) {
				StringBuilder sbPertis = new StringBuilder();
				String pertis = hmInvoicePerticulars.get(rs.getString("promntc_invoice_id"));
				if(pertis == null || pertis.equals("")) {
					sbPertis = new StringBuilder();
					sbPertis.append(rs.getString("invoice_particulars"));
				} else {
					sbPertis.append(hmInvoicePerticulars.get(rs.getString("promntc_invoice_id")));
					sbPertis.append(", "+rs.getString("invoice_particulars"));
				}
				hmInvoicePerticulars.put(rs.getString("promntc_invoice_id"), sbPertis.toString());
			} 
			rs.close();
			pst.close();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select pid.*,p.service,p.level_id,p.client_id from promntc_invoice_details pid, projectmntnc p where " +
				" pid.is_cancel=false and pid.pro_id = p.pro_id and pid.pro_id>0 ");
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" order by pid.promntc_invoice_id desc ");
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmInvoiceData = new LinkedHashMap<String, Map<String, String>>();
//			List<Map<String, String>> invoiceBillList = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				String days = uF.dateDifference(rs.getString("invoice_generated_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				Map<String, String> hmInvoice = new LinkedHashMap<String, String>();
				Map<String, String> hmInvoiceBill = hmInvoiceBillData.get(rs.getString("promntc_invoice_id"));
				if(hmInvoiceBill == null) hmInvoiceBill = new HashMap<String, String>();
				
				double tdsBooked = uF.parseToDouble(hmInvoiceBill.get(rs.getString("promntc_invoice_id")+"_BILL_TDS_DEDUCTED")) + uF.parseToDouble(hmInvoiceBill.get(rs.getString("promntc_invoice_id")+"_BILL_PREV_YEAR_TDS_DEDUCTED"));
				double outstandingAmt = uF.parseToDouble(rs.getString("invoice_amount")) - (uF.parseToDouble(hmInvoiceBill.get(rs.getString("promntc_invoice_id")+"_BILL_RECEIVED_AMOUNT")) + tdsBooked);
				hmInvoice.put("customername", CF.getClientNameById(con, rs.getString("client_id")));
				hmInvoice.put("invoiceno", rs.getString("invoice_code"));
				hmInvoice.put("particulars", uF.showData(hmInvoicePerticulars.get(rs.getString("promntc_invoice_id")), "-"));
				hmInvoice.put("date", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("invoiceamount", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("invoice_amount"))));
				hmInvoice.put("invoiceamountreceived", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInvoiceBill.get(rs.getString("promntc_invoice_id")+"_BILL_RECEIVED_AMOUNT"))));
				hmInvoice.put("tdsbooked",  uF.formatIntoTwoDecimalWithOutComma(tdsBooked));
				hmInvoice.put("outstandingamount", uF.formatIntoTwoDecimalWithOutComma(outstandingAmt));
				hmInvoice.put("receiptdate", uF.showData(hmBillReceiveDate.get(rs.getString("promntc_invoice_id")),"-"));
				if(outstandingAmt > 0) {
					hmInvoice.put("overduebydays", days+" days");
				} else {
					hmInvoice.put("overduebydays", "-");
				}
				hmInvoiceData.put(rs.getString("promntc_invoice_id"), hmInvoice);
//				invoiceBillList.add(hmInvoice);
			} 
			rs.close();
			pst.close();
//			System.out.println("hmInvoiceData ===>> " + hmInvoiceData);
			
			createXML("sbu", hmInvoiceData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void createXML(String strElement, Map<String, Map<String, String>> hmInvoiceData) {
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			Document document = documentBuilder.newDocument();

			Element orderElement = document.createElement(strElement);
			document.appendChild(orderElement);

			Set<String> set = hmInvoiceData.keySet();
			Iterator<String> iterator = set.iterator();

			while (iterator.hasNext()) {

				Element orderDetailElement = document.createElement("invoiceid");
				orderElement.appendChild(orderDetailElement);
				
				String key = iterator.next();

				Map<String, String> innerInvoiceData = hmInvoiceData.get(key);

				Set<String> set1 = innerInvoiceData.keySet();
				Iterator<String> iterator1 = set1.iterator();

				while (iterator1.hasNext()) {

					String key1 = (String) iterator1.next();
					Element detailElement1 = document.createElement(key1);
					detailElement1.appendChild(document.createTextNode(innerInvoiceData.get(key1)));
					orderDetailElement.appendChild(detailElement1);

				}				
			}

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(buffer);
			transformer.transform(source, result);

			response.setContentType("application/xml");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition",
					"attachment; filename=ARManagementReport.xml");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			out.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	
	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
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

	public String getGenerate() {
		return generate;
	}

	public void setGenerate(String generate) {
		this.generate = generate;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	private HttpServletRequest request;

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


}
