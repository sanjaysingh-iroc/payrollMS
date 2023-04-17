package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillClientAddress;
import com.konnect.jpms.select.FillClientPoc;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectAdHocInvoice extends ActionSupport implements ServletRequestAware,IStatements 
{
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private HttpServletRequest request;
	String strSessionEmpId;
	String strOrgId;
	String strUserType;
	
	List<FillClientPoc> clientPocList;
	String clientPoc;
	List<FillClientAddress> clientAddressList;
	String clientAddress;
	
	String invoiceType;
	
	String pro_id;
	String client_id;
	
	List<FillEmployee> projectOwnerList;
	String strProjectOwner;
	String invoiceGenDate;
	String invoiceCode;
	String submit;
	
	String othrClientName;
	String otheClientPoc;
	String otherClientAddress;
	String othrClntProject;

//	
	String strStartDate;
	String strEndDate;
	String strReferenceNo;
	
	String[] strParticulars;
	String[] strParticularsAmt;
	
	String[] strOtherParticulars;
	String[] strOtherParticularsAmt;
	
	String proDescription;
	
	String[] taxHead;
	String[] taxNameLabel;
	String[] taxHeadPercent;
	String[] taxHeadAmt;
	
	String otherDescription;
	
	String strTotalAmt;
	String particularTotalAmt;
	String totalAmt;

//	
	String strStartDateOtherCurr;
	String strEndDateOtherCurr;
	
	String strReferenceNoOtherCurr;
	
	String[] strParticularsINRCurr;
	String[] strParticularsAmtINRCurr;
	String[] strParticularsAmtOtherCurr;
	
	String[] strOtherParticularsINRCurr;
	String[] strOtherParticularsAmtINRCurr;
	String[] strOtherParticularsAmtOtherCurr;
	
	String proDescriptionOtherCurr;
	
	String[] taxHeadOtherCurr;
	String[] taxNameLabelOtherCurr;
	String[] taxHeadPercentOtherCurr;
	String[] taxHeadAmtINRCurr;
	String[] taxHeadAmtOtherCurr;
	
	String otherDescriptionOtherCurr;
	
	String strTotalAmtINRCurr;
	String strTotalAmtOtherCurr;
	
	String particularTotalAmtINRCurr;
	String totalAmtINRCurr;
	String particularTotalAmtOtherCurr;
	String totalAmtOtherCurr;

	List<FillBank> bankList;
	String bankName;
	
	List<FillClients> clientList;
	String client;
	
	String service;
	List<FillServices> serviceList;
	List<FillCurrency> currencyList;
	String strCurrency;
	
	String billingType;

//
	String strStartDateProrata;
	String strEndDateProrata;
	
	String strReferenceNoProrata;
	
	String[] strEmp;
	String[] billDailyHours;
	String[] billDaysHours;
	String[] empRate;
	String[] strEmpAmt;
	
	String[] strOtherParticularsProrata;
	String[] strOtherParticularsAmtProrata;
	
	String proDescriptionProrata;
	String otherDescriptionProrata;
	
	String strTotalAmtProrata;
	
//	
	String strStartDateOtherCurrProrata;
	String strEndDateOtherCurrProrata;
	
	String strReferenceNoOtherCurrProrata;
	
	String[] strEmpOtherCurr;
	String[] billDailyHoursINRCurr;
	String[] billDaysHoursINRCurr;
	String[] empRateINRCurr;
	String[] strEmpAmtINRCurr;
	String[] strEmpAmtOtherCurr;
	
	String[] strOtherParticularsINRCurrProrata;
	String[] strOtherParticularsAmtINRCurrProrata;
	String[] strOtherParticularsAmtOtherCurrProrata;
	
	String proDescriptionOtherCurrProrata;
	String otherDescriptionOtherCurrProrata;

	String strTotalAmtINRCurrProrata;
	String strTotalAmtOtherCurrProrata;
	
//	
	String operation;
	String invoiceId;
	
	String[] particularId;
	String[] otherParticularId;
	
	String[] resourceId;
	String[] resourceIdOtherCurr;
	
	String[] particularIdOtherCurr;
	String[] otherParticularIdOtherCurr;
	
	String[] taxHeadId;
	String[] taxHeadIdOtherCurr;
	
	String lblClientPoc;
	String lblClientAddress;
	String lblProjectOwner;
	String lblCurrency;
	String lblBankName;
	String lblClient;
	String lblService;
	String lblBillingType;
	String lblInvoiceCode;
	
	String proType;
	
	public String execute() {
		session=request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		UtilityFunctions uF=new UtilityFunctions();
		
		getProjectAdHocDetails(uF);
		
//		System.out.println("getOperation() ===>> " + getOperation());
//		System.out.println("getInvoiceId() ===>> " + getInvoiceId());
//		System.out.println("getBillingType() ===>> " + getBillingType());
		
		if(getSubmit()!=null && getSubmit().equals("Submit")) {
			
			if(getBillingType() != null && getBillingType().equals("1")) {
				insertProjectAdHocInvoice(uF);
			} else {
				insertProjectAdHocInvoiceProrata(uF);
			}
			return SUCCESS;
		} else if(getOperation() != null && getOperation().equals("E")) {
			String currType = getCurrencyType(getInvoiceId());
			if(getSubmit()!=null && getSubmit().equals("Update")) {
				if(getBillingType() != null && getBillingType().equals("1")) {
					updateProjectAdHocInvoice(uF);
				} else {
					updateProjectAdHocInvoiceProrata(uF);
				}
				return SUCCESS;
			}
			if(getBillingType() != null && getBillingType().equals("1")) {
				getAdHocInvoiceDetails(uF, currType);
			} else {
				getAdHocInvoiceProrataDetails(uF, currType);
			}
		}
		
		return LOAD;
	}

	
private void updateProjectAdHocInvoiceProrata(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);			
		Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
		
		int nClientId = 0;
		int nClientPocId = 0;
		int nClientAddressId = 0;
		int nProId = 0;
		
		if(getOthrClientName() != null && !getOthrClientName().equals("")) {
			pst = con.prepareStatement("insert into client_details (client_name, client_type, client_industry) values (?,?,?)");
			pst.setString(1, getOthrClientName());
			pst.setInt(2, CLIENT_TYPE_OTHER);
			pst.setString(3, "");
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(client_id) as client_id from client_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientId = rs.getInt("client_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into client_poc (contact_fname, contact_email, contact_number, contact_desig, contact_department, client_id) values (?,?,?,?,?,?)");
			pst.setString(1, uF.showData(getOtheClientPoc(),"N/A"));
			pst.setString(2, "N/A");
			pst.setString(3, "N/A");
			pst.setString(4, "N/A");
			pst.setString(5, "N/A");
			pst.setInt(6, nClientId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(poc_id) as poc_id from client_poc");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientPocId = rs.getInt("poc_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into client_address (client_address, client_id) values (?,?)");
			pst.setString(1, getOtherClientAddress()); 
			pst.setInt(2, nClientId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(client_address_id) as client_address_id from client_address");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientAddressId = rs.getInt("client_address_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into projectmntnc(pro_name,client_id,curr_id,billing_curr_id,added_by,project_type) values(?,?,?,?, ?,?)");
			pst.setString(1, getOthrClntProject());
			pst.setInt(2, nClientId);
			pst.setInt(3, uF.parseToInt(getStrCurrency()));
			pst.setInt(4, uF.parseToInt(getStrCurrency()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setInt(6, PROJECT_TYPE_OTHER);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select max(pro_id) as pro_id from projectmntnc");
			rs = pst.executeQuery();
			while(rs.next()) {
				nProId = rs.getInt("pro_id");
			}
			rs.close();
			pst.close();
		}
		
		pst = con.prepareStatement("update promntc_invoice_details set invoice_update_date=?,invoice_updated_by=?,pro_id=?,project_description=?," +
			"other_description=?,spoc_id=?,address_id=?,pro_owner_id=?,wlocation_id=?,depart_id=?,invoice_amount=?,particulars_total_amount=?," +
			"curr_id=?,bank_branch_id=?,invoice_type=?,client_id=?,service_id=?,reference_no_desc=?,oc_particulars_total_amount=?," +
			"oc_invoice_amount=?,adhoc_billing_type=? where promntc_invoice_id=? ");
		pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
		pst.setInt(2, uF.parseToInt(strSessionEmpId));
		if(uF.parseToInt(getPro_id()) > 0) {
			pst.setInt(3, uF.parseToInt(getPro_id()));
		} else {
			pst.setInt(3, nProId);
		}
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setString(4, getProDescriptionProrata());
			pst.setString(5, getOtherDescriptionProrata());
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setString(4, getProDescriptionOtherCurrProrata());
			pst.setString(5, getOtherDescriptionOtherCurrProrata());
		}
		if(uF.parseToInt(getClientPoc()) > 0) {
			pst.setInt(6, uF.parseToInt(getClientPoc()));
		} else {
			pst.setInt(6, nClientPocId);
		}
		if(uF.parseToInt(getClientAddress()) > 0) {
			pst.setInt(7, uF.parseToInt(getClientAddress()));
		} else {
			pst.setInt(7, nClientAddressId);
		}
		pst.setInt(8, uF.parseToInt(getStrProjectOwner()));
		pst.setInt(9, uF.parseToInt(hmEmpWlocationMap.get(getStrProjectOwner())));
		pst.setInt(10, uF.parseToInt(hmEmpDepartment.get(getStrProjectOwner()))); 
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setDouble(11, uF.parseToDouble(getTotalAmt()));
			pst.setDouble(12, uF.parseToDouble(getParticularTotalAmt()));
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setDouble(11, uF.parseToDouble(getTotalAmtINRCurr()));
			pst.setDouble(12, uF.parseToDouble(getParticularTotalAmtINRCurr()));
		}
		pst.setInt(13, uF.parseToInt(getStrCurrency()));
		pst.setInt(14, uF.parseToInt(getBankName())); 
		pst.setInt(15, ADHOC_INVOICE); 
		if(uF.parseToInt(getClient()) > 0) {
			pst.setInt(16, uF.parseToInt(getClient()));
		} else {
			pst.setInt(16, nClientId);
		}
		pst.setInt(17, uF.parseToInt(getService())); 
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setString(18, getStrReferenceNoProrata());
			pst.setDouble(19, uF.parseToDouble(getParticularTotalAmt()));
			pst.setDouble(20, uF.parseToDouble(getTotalAmt()));
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setString(18, getStrReferenceNoOtherCurrProrata());
			pst.setDouble(19, uF.parseToDouble(getParticularTotalAmtOtherCurr()));
			pst.setDouble(20, uF.parseToDouble(getTotalAmtOtherCurr()));
		}
		pst.setInt(21, uF.parseToInt(getBillingType()));
		pst.setInt(22, uF.parseToInt(getInvoiceId()));
		int x=pst.executeUpdate();
		pst.close();
		
		if(x>0) {
						
			
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				for(int i=0;getResourceId()!=null && i<getResourceId().length;i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set resource_name=?,days_hours=?,_rate=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=? where promntc_invoice_amt_id=?"); //day_or_hour=?,
					pst.setString(1, getStrEmp()[i]);
					pst.setDouble(2, uF.parseToDouble(getBillDailyHours()[i]));
					pst.setDouble(3, uF.parseToDouble(getEmpRate()[i]));
					pst.setDouble(4, uF.parseToDouble(getStrEmpAmt()[i]));
//					pst.setInt(6, uF.parseToInt(getBillDaysHours()[i]));
					pst.setDouble(5, uF.parseToDouble(getStrEmpAmt()[i]));
					pst.setInt(6, uF.parseToInt(getResourceId()[i]));
					pst.execute();
					pst.close();
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0;getResourceIdOtherCurr()!=null && i<getResourceIdOtherCurr().length;i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set resource_name=?,days_hours=?,_rate=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=? where promntc_invoice_amt_id=?"); //day_or_hour=?,
					pst.setString(1, getStrEmpOtherCurr()[i]);
					pst.setDouble(2, uF.parseToDouble(getBillDailyHoursINRCurr()[i]));
					pst.setDouble(3, uF.parseToDouble(getEmpRateINRCurr()[i]));
					pst.setDouble(4, uF.parseToDouble(getStrEmpAmtINRCurr()[i]));
//					pst.setInt(6, uF.parseToInt(getBillDaysHours()[i]));
					pst.setDouble(5, uF.parseToDouble(getStrEmpAmtOtherCurr()[i]));
					pst.setInt(6, uF.parseToInt(getResourceIdOtherCurr()[i]));
					pst.execute();
					pst.close();
				}
			}
			
			double dblTotOpe = 0;
			double dblTotOpeOtherCurr = 0;
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				for(int i=0; getOtherParticularId()!=null && i<getOtherParticularId().length; i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,head_type=? where promntc_invoice_amt_id=?");
					pst.setString(1, getStrOtherParticularsProrata()[i]);
					pst.setDouble(2, uF.parseToDouble(getStrOtherParticularsAmtProrata()[i]));
					pst.setDouble(3, uF.parseToDouble(getStrOtherParticularsAmtProrata()[i]));
					pst.setString(4, HEAD_OPE);
					pst.setInt(5, uF.parseToInt(getOtherParticularId()[i]));
					pst.executeUpdate();
					pst.close();
					dblTotOpe += uF.parseToDouble(getStrOtherParticularsAmtProrata()[i]);
					dblTotOpeOtherCurr += uF.parseToDouble(getStrOtherParticularsAmtProrata()[i]);
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0; getOtherParticularIdOtherCurr()!=null && i<getOtherParticularIdOtherCurr().length; i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,head_type=? where promntc_invoice_amt_id=?");
					pst.setString(1, getStrOtherParticularsINRCurrProrata()[i]);
					pst.setDouble(2, uF.parseToDouble(getStrOtherParticularsAmtINRCurrProrata()[i]));
					pst.setDouble(3, uF.parseToDouble(getStrOtherParticularsAmtOtherCurrProrata()[i]));
					pst.setString(4, HEAD_OPE);
					pst.setInt(5, uF.parseToInt(getOtherParticularIdOtherCurr()[i]));
					pst.executeUpdate();
					pst.close();
					dblTotOpe += uF.parseToDouble(getStrOtherParticularsAmtINRCurrProrata()[i]);
					dblTotOpeOtherCurr += uF.parseToDouble(getStrOtherParticularsAmtOtherCurrProrata()[i]);
				}
			}
			
			pst = con.prepareStatement("update promntc_invoice_details set other_amount=?, oc_other_amount=? where promntc_invoice_id=?");
			pst.setDouble(1, dblTotOpe);
			pst.setDouble(2, dblTotOpeOtherCurr);
			pst.setInt(3, uF.parseToInt(getInvoiceId())); 
			pst.executeUpdate();   
			pst.close();

			
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				for(int i=0; getTaxHeadId()!=null && i<getTaxHeadId().length; i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,tax_percent=?,head_type=? where promntc_invoice_amt_id=?");
					pst.setString(1, uF.showData(getTaxHead()[i], ""));
					pst.setDouble(2, uF.parseToDouble(getTaxHeadAmt()[i]));
					pst.setDouble(3, uF.parseToDouble(getTaxHeadAmt()[i]));
					pst.setDouble(4, uF.parseToDouble(getTaxHeadPercent()[i]));
					pst.setString(5, HEAD_TAX);
					pst.setInt(6, uF.parseToInt(getTaxHeadId()[i]));
					pst.executeUpdate();
					pst.close();
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0; getTaxHeadIdOtherCurr()!=null && i<getTaxHeadIdOtherCurr().length; i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,tax_percent=?,head_type=? where promntc_invoice_amt_id=?");
					pst.setString(1, uF.showData(getTaxHeadOtherCurr()[i], ""));
					pst.setDouble(2, uF.parseToDouble(getTaxHeadAmtINRCurr()[i]));
					pst.setDouble(3, uF.parseToDouble(getTaxHeadAmtOtherCurr()[i]));
					pst.setDouble(4, uF.parseToDouble(getTaxHeadPercentOtherCurr()[i]));
					pst.setString(5, HEAD_TAX);
					pst.setInt(6, uF.parseToInt(getTaxHeadIdOtherCurr()[i]));
					pst.executeUpdate();
					pst.close();
				}
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


private void updateProjectAdHocInvoice(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);			
		Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
		
//		String[] arr = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
		
		int nClientId = 0;
		int nClientPocId = 0;
		int nClientAddressId = 0;
		int nProId = 0;
		
		if(getOthrClientName() != null && !getOthrClientName().equals("")) {
			pst = con.prepareStatement("insert into client_details (client_name, client_type, client_industry) values (?,?,?)");
			pst.setString(1, getOthrClientName());
			pst.setInt(2, CLIENT_TYPE_OTHER);
			pst.setString(3, "");
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(client_id) as client_id from client_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientId = rs.getInt("client_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into client_poc (contact_fname, contact_email, contact_number, contact_desig, contact_department, client_id) values (?,?,?,?,?,?)");
			pst.setString(1, uF.showData(getOtheClientPoc(),"N/A"));
			pst.setString(2, "N/A");
			pst.setString(3, "N/A");
			pst.setString(4, "N/A");
			pst.setString(5, "N/A");
			pst.setInt(6, nClientId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(poc_id) as poc_id from client_poc");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientPocId = rs.getInt("poc_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into client_address (client_address, client_id) values (?,?)");
			pst.setString(1, getOtherClientAddress()); 
			pst.setInt(2, nClientId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(client_address_id) as client_address_id from client_address");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientAddressId = rs.getInt("client_address_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into projectmntnc(pro_name,client_id,curr_id,billing_curr_id,added_by,project_type) values(?,?,?,?, ?,?)");
			pst.setString(1, getOthrClntProject());
			pst.setInt(2, nClientId);
			pst.setInt(3, uF.parseToInt(getStrCurrency()));
			pst.setInt(4, uF.parseToInt(getStrCurrency()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setInt(6, PROJECT_TYPE_OTHER);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select max(pro_id) as pro_id from projectmntnc");
			rs = pst.executeQuery();
			while(rs.next()) {
				nProId = rs.getInt("pro_id");
			}
			rs.close();
			pst.close();
		}
		
		pst = con.prepareStatement("update promntc_invoice_details set invoice_update_date=?,invoice_updated_by=?,pro_id=?,project_description=?," +
			"other_description=?,spoc_id=?,address_id=?,pro_owner_id=?,wlocation_id=?,depart_id=?,invoice_amount=?,particulars_total_amount=?," +
			"curr_id=?,bank_branch_id=?,invoice_type=?,client_id=?,service_id=?,reference_no_desc=?,oc_particulars_total_amount=?," +
			"oc_invoice_amount=?,adhoc_billing_type=? where promntc_invoice_id=? ");
		pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
		pst.setInt(2, uF.parseToInt(strSessionEmpId));
		if(uF.parseToInt(getPro_id()) > 0) {
			pst.setInt(3, uF.parseToInt(getPro_id()));
		} else {
			pst.setInt(3, nProId);
		}
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setString(4, getProDescription());
			pst.setString(5, getOtherDescription());
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setString(4, getProDescriptionOtherCurr());
			pst.setString(5, getOtherDescriptionOtherCurr());
		}
		if(uF.parseToInt(getClientPoc()) > 0) {
			pst.setInt(6, uF.parseToInt(getClientPoc()));
		} else {
			pst.setInt(6, nClientPocId);
		}
		if(uF.parseToInt(getClientAddress()) > 0) {
			pst.setInt(7, uF.parseToInt(getClientAddress()));
		} else {
			pst.setInt(7, nClientAddressId);
		}
		pst.setInt(8, uF.parseToInt(getStrProjectOwner()));
		pst.setInt(9, uF.parseToInt(hmEmpWlocationMap.get(getStrProjectOwner())));
		pst.setInt(10, uF.parseToInt(hmEmpDepartment.get(getStrProjectOwner()))); 
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setDouble(11, uF.parseToDouble(getTotalAmt()));
			pst.setDouble(12, uF.parseToDouble(getParticularTotalAmt()));
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setDouble(11, uF.parseToDouble(getTotalAmtINRCurr()));
			pst.setDouble(12, uF.parseToDouble(getParticularTotalAmtINRCurr()));
		}
		pst.setInt(13, uF.parseToInt(getStrCurrency()));
		pst.setInt(14, uF.parseToInt(getBankName())); 
		pst.setInt(15, ADHOC_INVOICE); 
		if(uF.parseToInt(getClient()) > 0) {
			pst.setInt(16, uF.parseToInt(getClient()));
		} else {
			pst.setInt(16, nClientId);
		}
		pst.setInt(17, uF.parseToInt(getService())); 
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setString(18, getStrReferenceNo());
			pst.setDouble(19, uF.parseToDouble(getParticularTotalAmt()));
			pst.setDouble(20, uF.parseToDouble(getTotalAmt()));
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setString(18, getStrReferenceNoOtherCurr());
			pst.setDouble(19, uF.parseToDouble(getParticularTotalAmtOtherCurr()));
			pst.setDouble(20, uF.parseToDouble(getTotalAmtOtherCurr()));
		}
		pst.setInt(21, uF.parseToInt(getBillingType()));
		pst.setInt(22, uF.parseToInt(getInvoiceId()));
		int x=pst.executeUpdate();
		pst.close();
		
		if(x>0) {
						
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				for(int i=0;getParticularId()!=null && i<getParticularId().length;i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,head_type=? where promntc_invoice_amt_id=?");
					pst.setString(1, getStrParticulars()[i]);
					pst.setDouble(2, uF.parseToDouble(getStrParticularsAmt()[i]));
					pst.setDouble(3, uF.parseToDouble(getStrParticularsAmt()[i]));
					pst.setString(4, HEAD_PARTI);
					pst.setInt(5, uF.parseToInt(getParticularId()[i]));
					pst.executeUpdate();
					pst.close();
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0;getParticularIdOtherCurr()!=null && i<getParticularIdOtherCurr().length;i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,head_type=? where promntc_invoice_amt_id=?");
					pst.setString(1, getStrParticularsINRCurr()[i]);
					pst.setDouble(2, uF.parseToDouble(getStrParticularsAmtINRCurr()[i]));
					pst.setDouble(3, uF.parseToDouble(getStrParticularsAmtOtherCurr()[i]));
					pst.setString(4, HEAD_PARTI);
					pst.setInt(5, uF.parseToInt(getParticularIdOtherCurr()[i]));
					pst.executeUpdate();
					pst.close();
				}
			}
			
			double dblTotOpe = 0;
			double dblTotOpeOtherCurr = 0;
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				for(int i=0; getOtherParticularId()!=null && i<getOtherParticularId().length; i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,head_type=? where promntc_invoice_amt_id=?");
					pst.setString(1, getStrOtherParticulars()[i]);
					pst.setDouble(2, uF.parseToDouble(getStrOtherParticularsAmt()[i]));
					pst.setDouble(3, uF.parseToDouble(getStrOtherParticularsAmt()[i]));
					pst.setString(4, HEAD_OPE);
					pst.setInt(5, uF.parseToInt(getOtherParticularId()[i]));
					pst.executeUpdate();
					pst.close();
					dblTotOpe += uF.parseToDouble(getStrOtherParticularsAmt()[i]);
					dblTotOpeOtherCurr += uF.parseToDouble(getStrOtherParticularsAmt()[i]);
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0; getOtherParticularIdOtherCurr()!=null && i<getOtherParticularIdOtherCurr().length; i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,head_type=? where promntc_invoice_amt_id=?");
					pst.setString(1, getStrOtherParticularsINRCurr()[i]);
					pst.setDouble(2, uF.parseToDouble(getStrOtherParticularsAmtINRCurr()[i]));
					pst.setDouble(3, uF.parseToDouble(getStrOtherParticularsAmtOtherCurr()[i]));
					pst.setString(4, HEAD_OPE);
					pst.setInt(5, uF.parseToInt(getOtherParticularIdOtherCurr()[i]));
					pst.executeUpdate();
					pst.close();
					dblTotOpe += uF.parseToDouble(getStrOtherParticularsAmtINRCurr()[i]);
					dblTotOpeOtherCurr += uF.parseToDouble(getStrOtherParticularsAmtOtherCurr()[i]);
				}
			}
			
			pst = con.prepareStatement("update promntc_invoice_details set other_amount=?, oc_other_amount=? where promntc_invoice_id=?");
			pst.setDouble(1, dblTotOpe);
			pst.setDouble(2, dblTotOpeOtherCurr);
			pst.setInt(3, uF.parseToInt(getInvoiceId())); 
			pst.executeUpdate();   
			pst.close();

			
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				for(int i=0; getTaxHeadId()!=null && i<getTaxHeadId().length; i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,tax_percent=?,head_type=?,invoice_particulars_label=? where promntc_invoice_amt_id=?");
					pst.setString(1, uF.showData(getTaxHead()[i], ""));
					pst.setDouble(2, uF.parseToDouble(getTaxHeadAmt()[i]));
					pst.setDouble(3, uF.parseToDouble(getTaxHeadAmt()[i]));
					pst.setDouble(4, uF.parseToDouble(getTaxHeadPercent()[i]));
					pst.setString(5, HEAD_TAX);
					pst.setString(6, uF.showData(getTaxNameLabel()[i], ""));
					pst.setInt(7, uF.parseToInt(getTaxHeadId()[i]));
					pst.executeUpdate();
					pst.close();
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0; getTaxHeadIdOtherCurr()!=null && i<getTaxHeadIdOtherCurr().length; i++) {
					pst = con.prepareStatement("update promntc_invoice_amt_details set invoice_particulars=?,invoice_particulars_amount=?," +
						"oc_invoice_particulars_amount=?,tax_percent=?,head_type=?,invoice_particulars_label=? where promntc_invoice_amt_id=?");
					pst.setString(1, uF.showData(getTaxHeadOtherCurr()[i], ""));
					pst.setDouble(2, uF.parseToDouble(getTaxHeadAmtINRCurr()[i]));
					pst.setDouble(3, uF.parseToDouble(getTaxHeadAmtOtherCurr()[i]));
					pst.setDouble(4, uF.parseToDouble(getTaxHeadPercentOtherCurr()[i]));
					pst.setString(5, HEAD_TAX);
					pst.setString(6, uF.showData(getTaxNameLabelOtherCurr()[i], ""));
					pst.setInt(7, uF.parseToInt(getTaxHeadIdOtherCurr()[i]));
					pst.executeUpdate();
					pst.close();
				}
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


private void getAdHocInvoiceProrataDetails(UtilityFunctions uF, String currType) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		
		con = db.makeConnection(con);
		
		Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
		pst = con.prepareStatement("select * from promntc_invoice_details where promntc_invoice_id = ?");
		pst.setInt(1, uF.parseToInt(getInvoiceId()));
		rs = pst.executeQuery();
		Map<String, String> hmCurr = new HashMap<String, String>();
		while (rs.next()) {
			hmCurr = hmCurrencyDetails.get(rs.getString("curr_id"));
			if(hmCurr == null) hmCurr = new HashMap<String, String>();
			
			setInvoiceGenDate(uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, DATE_FORMAT));
			setLblClientPoc(uF.showData(CF.getClientSPOCNameById(con, rs.getString("spoc_id")), "-"));
			setLblClientAddress(uF.showData(CF.getClientAddressById(con, rs.getString("address_id")), "-"));
			setLblProjectOwner(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("pro_owner_id")), "-"));
			setLblCurrency(uF.showData(hmCurr.get("LONG_CURR"),"-"));
			setLblBankName(uF.showData(CF.getBankNameByBankId(con, uF, rs.getString("bank_branch_id")), "-"));
			setLblClient(uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "-"));
			setLblService(uF.showData(CF.getServiceNameById(con, rs.getString("service_id")), "-"));
			setLblBillingType(uF.showData(uF.getAdHocBillingType(rs.getString("adhoc_billing_type")), "-"));
			setLblInvoiceCode(uF.showData(rs.getString("invoice_code"), "-"));
			setInvoiceCode(uF.showData(rs.getString("invoice_code"), "-"));
			
			setClientPoc(uF.showData(rs.getString("spoc_id"), "0"));
			setClientAddress(uF.showData(rs.getString("address_id"), "0"));
			setStrProjectOwner(uF.showData(rs.getString("pro_owner_id"), "0"));
			setStrCurrency(uF.showData(rs.getString("curr_id"), "0"));
			setBankName(uF.showData(rs.getString("bank_branch_id"), "0"));
			setClient(uF.showData(rs.getString("client_id"), "0"));
			setService(uF.showData(rs.getString("service_id"), "0"));
			setBillingType(uF.showData(rs.getString("adhoc_billing_type"), "0"));
			
			if (currType != null && currType.equals(INR_CURR_ID)) {
				setStrStartDateProrata(uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, DATE_FORMAT));
				setStrEndDateProrata(uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, DATE_FORMAT));
				setProDescriptionProrata(uF.showData(rs.getString("project_description"), ""));
				setOtherDescriptionProrata(uF.showData(rs.getString("other_description"), ""));
				setTotalAmt(uF.showData(rs.getString("invoice_amount"), "0"));
				setStrTotalAmtProrata(uF.showData(rs.getString("particulars_total_amount"), "0"));
				setParticularTotalAmt(uF.showData(rs.getString("particulars_total_amount"), "0"));
				setStrReferenceNoProrata(uF.showData(rs.getString("reference_no_desc"), ""));
				
			} else {
				setStrStartDateOtherCurrProrata(uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, DATE_FORMAT));
				setStrEndDateOtherCurrProrata(uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, DATE_FORMAT));
				setProDescriptionOtherCurrProrata(uF.showData(rs.getString("project_description"), ""));
				setOtherDescriptionOtherCurrProrata(uF.showData(rs.getString("other_description"), ""));
				setTotalAmtINRCurr(uF.showData(rs.getString("invoice_amount"), "0"));
				setStrTotalAmtINRCurrProrata(uF.showData(rs.getString("particulars_total_amount"), "0"));
				setParticularTotalAmtINRCurr(uF.showData(rs.getString("particulars_total_amount"), "0"));
				setStrReferenceNoOtherCurrProrata(uF.showData(rs.getString("reference_no_desc"), ""));
				setStrTotalAmtOtherCurrProrata(uF.showData(rs.getString("oc_particulars_total_amount"), "0"));
				setParticularTotalAmtOtherCurr(uF.showData(rs.getString("oc_particulars_total_amount"), "0"));
				setTotalAmtOtherCurr(uF.showData(rs.getString("oc_invoice_amount"), "0"));
			}
		}
		
		clientPocList = new FillClientPoc(request).fillClientPoc(getClient());
		clientAddressList =new FillClientAddress(request).fillClientAddress(getClient(), uF);
		rs.close();
		pst.close();
		
		
		List<List<String>> otherPartiListProrata = new ArrayList<List<String>>();
		List<List<String>> resourcesList = new ArrayList<List<String>>();
		List<List<String>> taxHeadsList = new ArrayList<List<String>>();
		List<List<String>> resourcesListOtherCurr = new ArrayList<List<String>>();
		List<List<String>> otherPartiListOtherCurrProrata = new ArrayList<List<String>>();
		List<List<String>> taxHeadsListOtherCurr = new ArrayList<List<String>>();
		
				if (currType != null && currType.equals(INR_CURR_ID)) {
					
					pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type is null");
					pst.setInt(1, uF.parseToInt(getInvoiceId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("promntc_invoice_id"));
						innerList.add(rs.getString("promntc_invoice_amt_id"));
						innerList.add(uF.showData(rs.getString("resource_name"), ""));
						innerList.add(uF.showData(rs.getString("days_hours"), ""));
						innerList.add(uF.showData(uF.getProrataBillDayHourMonthById(rs.getString("day_or_hour")), ""));
						innerList.add(uF.showData(rs.getString("_rate"), ""));
						innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
						resourcesList.add(innerList);
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='OPE'");
					pst.setInt(1, uF.parseToInt(getInvoiceId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("promntc_invoice_id"));
						innerList.add(rs.getString("promntc_invoice_amt_id"));
						innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
						innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
						innerList.add(uF.showData(rs.getString("head_type"), ""));
						otherPartiListProrata.add(innerList);
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='TAX'");
					
					pst.setInt(1, uF.parseToInt(getInvoiceId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("promntc_invoice_id"));
						innerList.add(rs.getString("promntc_invoice_amt_id"));
						innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
						innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
						innerList.add(uF.showData(rs.getString("head_type"), ""));
						innerList.add(uF.showData(rs.getString("tax_percent"), "0"));
						innerList.add(uF.showData(rs.getString("invoice_particulars_label"), ""));
						taxHeadsList.add(innerList);
					}
					rs.close();
					pst.close();
					
			} else {
				pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type is null");
				pst.setInt(1, uF.parseToInt(getInvoiceId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("promntc_invoice_id"));
					innerList.add(rs.getString("promntc_invoice_amt_id"));
					innerList.add(uF.showData(rs.getString("resource_name"), ""));
					innerList.add(uF.showData(rs.getString("days_hours"), ""));
					innerList.add(uF.showData(uF.getProrataBillDayHourMonthById(rs.getString("day_or_hour")), ""));
					innerList.add(uF.showData(rs.getString("_rate"), ""));
					innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("oc_invoice_particulars_amount"), "0"));
					resourcesListOtherCurr.add(innerList);
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='OPE'");
				pst.setInt(1, uF.parseToInt(getInvoiceId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("promntc_invoice_id"));
					innerList.add(rs.getString("promntc_invoice_amt_id"));
					innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
					innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("oc_invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("head_type"), ""));
					otherPartiListOtherCurrProrata.add(innerList);
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='TAX'");
				pst.setInt(1, uF.parseToInt(getInvoiceId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("promntc_invoice_id"));
					innerList.add(rs.getString("promntc_invoice_amt_id"));
					innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
					innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("oc_invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("head_type"), ""));
					innerList.add(uF.showData(rs.getString("tax_percent"), "0"));
					innerList.add(uF.showData(rs.getString("invoice_particulars_label"), ""));
					taxHeadsListOtherCurr.add(innerList);
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("resourcesList", resourcesList);
			request.setAttribute("otherPartiListProrata", otherPartiListProrata);
			request.setAttribute("taxHeadsList", taxHeadsList);
			
			request.setAttribute("resourcesListOtherCurr", resourcesListOtherCurr);
			request.setAttribute("otherPartiListOtherCurrProrata", otherPartiListOtherCurrProrata);
			request.setAttribute("taxHeadsListOtherCurr", taxHeadsListOtherCurr);
			
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}


private void getAdHocInvoiceDetails(UtilityFunctions uF, String currType) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		
		con = db.makeConnection(con);
		
		Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
		pst = con.prepareStatement("select * from promntc_invoice_details where promntc_invoice_id = ?");
		pst.setInt(1, uF.parseToInt(getInvoiceId()));
		rs = pst.executeQuery();
		Map<String, String> hmCurr = new HashMap<String, String>();
		while (rs.next()) {
			hmCurr = hmCurrencyDetails.get(rs.getString("curr_id"));
			if(hmCurr == null) hmCurr = new HashMap<String, String>();
			
			setInvoiceGenDate(uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, DATE_FORMAT));
			setLblClientPoc(uF.showData(CF.getClientSPOCNameById(con, rs.getString("spoc_id")), "-"));
			setLblClientAddress(uF.showData(CF.getClientAddressById(con, rs.getString("address_id")), "-"));
			setLblProjectOwner(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("pro_owner_id")), "-"));
			setLblCurrency(uF.showData(hmCurr.get("LONG_CURR"),"-"));
			setLblBankName(uF.showData(CF.getBankNameByBankId(con, uF, rs.getString("bank_branch_id")), "-"));
			setLblClient(uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "-"));
			setLblService(uF.showData(CF.getServiceNameById(con, rs.getString("service_id")), "-"));
			setLblBillingType(uF.showData(uF.getAdHocBillingType(rs.getString("adhoc_billing_type")), "-"));
			setLblInvoiceCode(uF.showData(rs.getString("invoice_code"), "-"));
			setInvoiceCode(uF.showData(rs.getString("invoice_code"), "-"));
			
			setClientPoc(uF.showData(rs.getString("spoc_id"), "0"));
			setClientAddress(uF.showData(rs.getString("address_id"), "0"));
			setStrProjectOwner(uF.showData(rs.getString("pro_owner_id"), "0"));
			setStrCurrency(uF.showData(rs.getString("curr_id"), "0"));
			setBankName(uF.showData(rs.getString("bank_branch_id"), "0"));
			setClient(uF.showData(rs.getString("client_id"), "0"));
			setService(uF.showData(rs.getString("service_id"), "0"));
			setBillingType(uF.showData(rs.getString("adhoc_billing_type"), "0"));
			
			if (currType != null && currType.equals(INR_CURR_ID)) {
				setStrStartDate(uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, DATE_FORMAT));
				setProDescription(uF.showData(rs.getString("project_description"), ""));
				setOtherDescription(uF.showData(rs.getString("other_description"), ""));
				setTotalAmt(uF.showData(rs.getString("invoice_amount"), "0"));
				setStrTotalAmt(uF.showData(rs.getString("particulars_total_amount"), "0"));
				setParticularTotalAmt(uF.showData(rs.getString("particulars_total_amount"), "0"));
				setStrReferenceNo(uF.showData(rs.getString("reference_no_desc"), ""));
				
			} else {
				setStrStartDateOtherCurr(uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, DATE_FORMAT));
				setStrEndDateOtherCurr(uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, DATE_FORMAT));
				setProDescriptionOtherCurr(uF.showData(rs.getString("project_description"), ""));
				setOtherDescriptionOtherCurr(uF.showData(rs.getString("other_description"), ""));
				setTotalAmtINRCurr(uF.showData(rs.getString("invoice_amount"), "0"));
				setStrTotalAmtINRCurr(uF.showData(rs.getString("particulars_total_amount"), "0"));
				setParticularTotalAmtINRCurr(uF.showData(rs.getString("particulars_total_amount"), "0"));
				setStrReferenceNoOtherCurr(uF.showData(rs.getString("reference_no_desc"), ""));
				setStrTotalAmtOtherCurr(uF.showData(rs.getString("oc_particulars_total_amount"), "0"));
				setParticularTotalAmtOtherCurr(uF.showData(rs.getString("oc_particulars_total_amount"), "0"));
				setTotalAmtOtherCurr(uF.showData(rs.getString("oc_invoice_amount"), "0"));
			}
		}
		
		clientPocList = new FillClientPoc(request).fillClientPoc(getClient());
		clientAddressList =new FillClientAddress(request).fillClientAddress(getClient(), uF);
		rs.close();
		pst.close();
		
		
		List<List<String>> particularsList = new ArrayList<List<String>>();
		List<List<String>> taxHeadsList = new ArrayList<List<String>>();
		List<List<String>> particularsListOtherCurr = new ArrayList<List<String>>();
		List<List<String>> taxHeadsListOtherCurr = new ArrayList<List<String>>();
		
				if (currType != null && currType.equals(INR_CURR_ID)) {
					
					pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='PARTI'");
					
					pst.setInt(1, uF.parseToInt(getInvoiceId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("promntc_invoice_id"));
						innerList.add(rs.getString("promntc_invoice_amt_id"));
						innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
						innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
						innerList.add(uF.showData(rs.getString("head_type"), ""));
						particularsList.add(innerList);
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='OPE'");
					pst.setInt(1, uF.parseToInt(getInvoiceId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("promntc_invoice_id"));
						innerList.add(rs.getString("promntc_invoice_amt_id"));
						innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
						innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
						innerList.add(uF.showData(rs.getString("head_type"), ""));
						particularsList.add(innerList);
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='TAX'");
					
					pst.setInt(1, uF.parseToInt(getInvoiceId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("promntc_invoice_id"));
						innerList.add(rs.getString("promntc_invoice_amt_id"));
						innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
						innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
						innerList.add(uF.showData(rs.getString("head_type"), ""));
						innerList.add(uF.showData(rs.getString("tax_percent"), "0"));
						innerList.add(uF.showData(rs.getString("invoice_particulars_label"), ""));
						taxHeadsList.add(innerList);
					}
					rs.close();
					pst.close();
					
			} else {
				pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='PARTI'");
				pst.setInt(1, uF.parseToInt(getInvoiceId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("promntc_invoice_id"));
					innerList.add(rs.getString("promntc_invoice_amt_id"));
					innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
					innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("oc_invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("head_type"), ""));
					particularsListOtherCurr.add(innerList);
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='OPE'");
				pst.setInt(1, uF.parseToInt(getInvoiceId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("promntc_invoice_id"));
					innerList.add(rs.getString("promntc_invoice_amt_id"));
					innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
					innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("oc_invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("head_type"), ""));
					particularsListOtherCurr.add(innerList);
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id = ? and head_type='TAX'");
				pst.setInt(1, uF.parseToInt(getInvoiceId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("promntc_invoice_id"));
					innerList.add(rs.getString("promntc_invoice_amt_id"));
					innerList.add(uF.showData(rs.getString("invoice_particulars"), ""));
					innerList.add(uF.showData(rs.getString("invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("oc_invoice_particulars_amount"), "0"));
					innerList.add(uF.showData(rs.getString("head_type"), ""));
					innerList.add(uF.showData(rs.getString("tax_percent"), "0"));
					innerList.add(uF.showData(rs.getString("invoice_particulars_label"), ""));
					taxHeadsListOtherCurr.add(innerList);
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("particularsList", particularsList);
			request.setAttribute("taxHeadsList", taxHeadsList);
			
			request.setAttribute("particularsListOtherCurr", particularsListOtherCurr);
			request.setAttribute("taxHeadsListOtherCurr", taxHeadsListOtherCurr);
			
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}



private void insertProjectAdHocInvoiceProrata(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	
	try {
		//Map<String,String> hmProjectDetails=(Map<String,String>)request.getAttribute("hmProjectDetails");
		
		con = db.makeConnection(con);
		
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);			
		Map<String, Map<String, String>> hmWorkLocation =CF.getWorkLocationMap(con);
		Map<String, String> hmLocation=hmWorkLocation.get(hmEmpWlocationMap.get(getStrProjectOwner()));
		Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);
		Map<String, String> hmDept =CF.getDeptMap(con);
//		Map<String,String> hmTaxMiscSetting=(Map<String,String>)request.getAttribute("hmTaxMiscSetting");
		
		String[] arr=CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
//		String locationCode=hmLocation.get("WL_NAME").substring(0,3);
		String locationCode=hmLocation.get("WL_NAME");
		String departCode="";
		if(hmDept.get(hmEmpDepartment.get(getStrProjectOwner()))!=null && hmDept.get(hmEmpDepartment.get(getStrProjectOwner())).contains(" ")) {
			String[] temp=hmDept.get(hmEmpDepartment.get(getStrProjectOwner())).toUpperCase().split(" ");
			for(int i=0;i<temp.length;i++) {
				departCode+=temp[i].substring(0,1);
			}
		} else if(hmDept.get(hmEmpDepartment.get(getStrProjectOwner()))!=null) {
			
			departCode=hmDept.get(hmEmpDepartment.get(getStrProjectOwner())).length() > 3 ? hmDept.get(hmEmpDepartment.get(getStrProjectOwner())).substring(0,3) : hmDept.get(hmEmpDepartment.get(getStrProjectOwner()));
		}
		
		int count=0;
//		pst=con.prepareStatement("select count(promntc_invoice_id) as count from promntc_invoice_details where wlocation_id=? and depart_id=?");
		pst=con.prepareStatement("select max(invoice_no) as invoice_no from promntc_invoice_details where wlocation_id=?");
		pst.setInt(1, uF.parseToInt(hmEmpWlocationMap.get(getStrProjectOwner())));
//		pst.setInt(2, uF.parseToInt(hmEmpDepartment.get(getStrProjectOwner())));
		rs=pst.executeQuery();
		while(rs.next()) {
			count=rs.getInt("invoice_no");
		}
		rs.close();
		pst.close();
		
		count++;
		
		String invoiceCode = count +"-" + uF.getDateFormat(arr[0], DATE_FORMAT, "yyyy") + "-" + uF.getDateFormat(arr[1], DATE_FORMAT, "yy") + "/" + locationCode.toUpperCase(); // + "/" + departCode.toUpperCase()
		
		int nClientId = 0;
		int nClientPocId = 0;
		int nClientAddressId = 0;
		int nProId = 0;
		if(getOthrClientName() != null && !getOthrClientName().equals("")) {
			pst = con.prepareStatement("insert into client_details (client_name, client_type, client_industry) values (?,?,?)");
			pst.setString(1, getOthrClientName());
			pst.setInt(2, CLIENT_TYPE_OTHER);
			pst.setString(3, "");
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select max(client_id) as client_id from client_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientId = rs.getInt("client_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into client_poc (contact_name, contact_email, contact_number, contact_desig, contact_department, client_id) values (?,?,?,?,?,?)");
			pst.setString(1, uF.showData(getOtheClientPoc(),"N/A"));
			pst.setString(2, "N/A"); 
			pst.setString(3, "N/A"); 
			pst.setString(4, "N/A"); 
			pst.setString(5, "N/A"); 
			pst.setInt(6, nClientId);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select max(poc_id) as poc_id from client_poc");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientPocId = rs.getInt("poc_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into client_address (client_address, client_id) values (?,?)");
			pst.setString(1, getOtherClientAddress()); 
			pst.setInt(2, nClientId);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select max(client_address_id) as client_address_id from client_address");
			rs = pst.executeQuery();
			while(rs.next()) {
				nClientAddressId = rs.getInt("client_address_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into projectmntnc(pro_name,client_id,curr_id,billing_curr_id,added_by,project_type) values(?,?,?,?, ?,?)");
			pst.setString(1, getOthrClntProject());
			pst.setInt(2, nClientId);
			pst.setInt(3, uF.parseToInt(getStrCurrency()));
			pst.setInt(4, uF.parseToInt(getStrCurrency()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setInt(6, PROJECT_TYPE_OTHER);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select max(pro_id) as pro_id from projectmntnc");
			rs = pst.executeQuery();
			while(rs.next()) {
				nProId = rs.getInt("pro_id");
			}
			rs.close();
			pst.close();			
		}
		
		pst = con.prepareStatement("insert into promntc_invoice_details(invoice_generated_date,invoice_generated_by,invoice_from_date," +
			"invoice_to_date,pro_id,invoice_code,project_description,other_description,spoc_id,address_id,pro_owner_id,financial_start_date," +
			"financial_end_date,wlocation_id,depart_id,invoice_amount,particulars_total_amount,curr_id,other_amount,other_particular," +
			"bank_branch_id,invoice_type,client_id,service_id,standard_tax,education_tax,service_tax,reference_no_desc,oc_particulars_total_amount," +
				"oc_invoice_amount,oc_other_amount,adhoc_billing_type,pro_freq_id,entry_date,invoice_template_id)" +
			"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
		pst.setDate(1, uF.getDateFormat(getInvoiceGenDate(), DATE_FORMAT));
		pst.setInt(2, uF.parseToInt(strSessionEmpId));
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setDate(3, uF.getDateFormat(getStrStartDateProrata(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrEndDateProrata(), DATE_FORMAT));
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setDate(3, uF.getDateFormat(getStrStartDateOtherCurrProrata(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrEndDateOtherCurrProrata(), DATE_FORMAT));
		}
		if(uF.parseToInt(getPro_id()) > 0) {
			pst.setInt(5, uF.parseToInt(getPro_id()));
		} else {
			pst.setInt(5, nProId);
		}
		pst.setString(6, invoiceCode);
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setString(7, getProDescriptionProrata());
			pst.setString(8, getOtherDescriptionProrata());
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setString(7, getProDescriptionOtherCurrProrata());
			pst.setString(8, getOtherDescriptionOtherCurrProrata());
		}
		if(uF.parseToInt(getClientPoc()) > 0) {
			pst.setInt(9, uF.parseToInt(getClientPoc()));
		} else {
			pst.setInt(9, nClientPocId);
		}
		if(uF.parseToInt(getClientAddress()) > 0) {
			pst.setInt(10, uF.parseToInt(getClientAddress()));
		} else {
			pst.setInt(10, nClientAddressId);
		}
		pst.setInt(11, uF.parseToInt(getStrProjectOwner()));
		pst.setDate(12, uF.getDateFormat(arr[0], DATE_FORMAT));
		pst.setDate(13, uF.getDateFormat(arr[1], DATE_FORMAT));
		pst.setInt(14, uF.parseToInt(hmEmpWlocationMap.get(getStrProjectOwner())));
		pst.setInt(15, uF.parseToInt(hmEmpDepartment.get(getStrProjectOwner()))); 
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setDouble(16, uF.parseToDouble(getTotalAmt()));
			pst.setDouble(17, uF.parseToDouble(getParticularTotalAmt()));
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setDouble(16, uF.parseToDouble(getTotalAmtINRCurr()));
			pst.setDouble(17, uF.parseToDouble(getParticularTotalAmtINRCurr()));
		}
		pst.setInt(18, uF.parseToInt(getStrCurrency()));
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
//			pst.setDouble(19, uF.parseToDouble(getStrOtherParticularsAmtProrata()));
//			pst.setString(20, getStrOtherParticularsProrata());
			pst.setDouble(19, 0);
			pst.setString(20, "");
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
//			pst.setDouble(19, uF.parseToDouble(getStrOtherParticularsAmtINRCurrProrata()));
//			pst.setString(20, getStrOtherParticularsINRCurrProrata());
			pst.setDouble(19, 0);
			pst.setString(20, "");
		}
		pst.setInt(21, uF.parseToInt(getBankName())); 
		pst.setInt(22, ADHOC_PRORETA_INVOICE); 
		if(uF.parseToInt(getClient()) > 0) {
			pst.setInt(23, uF.parseToInt(getClient()));
		} else {
			pst.setInt(23, nClientId);
		}
		pst.setInt(24, uF.parseToInt(getService())); 
//		pst.setDouble(25, uF.parseToDouble(hmTaxMiscSetting.get("STANDARD_TAX")));
//		pst.setDouble(26, uF.parseToDouble(hmTaxMiscSetting.get("EDUCATION_TAX")));
//		pst.setDouble(27, uF.parseToDouble(hmTaxMiscSetting.get("SERVICE_TAX")));
		pst.setDouble(25, 0);
		pst.setDouble(26, 0);
		pst.setDouble(27, 0);
		if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
			pst.setString(28, getStrReferenceNoProrata());
			pst.setDouble(29, uF.parseToDouble(getParticularTotalAmt()));
			pst.setDouble(30, uF.parseToDouble(getTotalAmt()));
//			pst.setDouble(31, uF.parseToDouble(getStrOtherParticularsAmtProrata()));
			pst.setDouble(31, 0);
		} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
			pst.setString(28, getStrReferenceNoOtherCurrProrata());
			pst.setDouble(29, uF.parseToDouble(getParticularTotalAmtOtherCurr()));
			pst.setDouble(30, uF.parseToDouble(getTotalAmtOtherCurr()));
//			pst.setDouble(31, uF.parseToDouble(getStrOtherParticularsAmtOtherCurrProrata()));
			pst.setDouble(31, 0);
		}
		pst.setInt(32, uF.parseToInt(getBillingType()));
		pst.setInt(33, 0);
		pst.setDate(34, uF.getCurrentDate(CF.getStrTimeZone()));
		pst.setInt(35, 0);
		int x=pst.executeUpdate();
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
			
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				for(int i=0;getStrEmp()!=null && i<getStrEmp().length; i++) {
					if(getStrEmp()[i] != null && !getStrEmp()[i].equals("")) {
						pst = con.prepareStatement("insert into promntc_invoice_amt_details(resource_name,days_hours,_rate,invoice_particulars_amount," +
							"promntc_invoice_id,day_or_hour,oc_invoice_particulars_amount)values(?,?,?,?, ?,?,?)");
						pst.setString(1, getStrEmp()[i]);
						if(uF.parseToInt(getBillDaysHours()[i]) == 2) {
							pst.setDouble(2, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getBillDailyHours()[i])));
						} else {
							pst.setDouble(2, uF.parseToDouble(getBillDailyHours()[i]));
						}
						pst.setDouble(3, uF.parseToDouble(getEmpRate()[i]));
						pst.setDouble(4, uF.parseToDouble(getStrEmpAmt()[i]));
						pst.setInt(5, promntc_invoice_id);
						pst.setInt(6, uF.parseToInt(getBillDaysHours()[i]));
						pst.setDouble(7, uF.parseToDouble(getStrEmpAmt()[i]));
						pst.execute();
						pst.close();
					}
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0;getStrEmpOtherCurr()!=null && i<getStrEmpOtherCurr().length; i++) {
					if(getStrEmpOtherCurr()[i] != null && !getStrEmpOtherCurr()[i].equals("")) {
						pst = con.prepareStatement("insert into promntc_invoice_amt_details(resource_name,days_hours,_rate,invoice_particulars_amount," +
							"promntc_invoice_id,day_or_hour,oc_invoice_particulars_amount)values(?,?,?,?, ?,?,?)");
						pst.setString(1, getStrEmpOtherCurr()[i]);
						if(uF.parseToInt(getBillDaysHoursINRCurr()[i]) == 2) {
							pst.setDouble(2, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getBillDailyHoursINRCurr()[i])));
						} else {
							pst.setDouble(2, uF.parseToDouble(getBillDailyHoursINRCurr()[i]));
						}
						pst.setDouble(3, uF.parseToDouble(getEmpRateINRCurr()[i]));
						pst.setDouble(4, uF.parseToDouble(getStrEmpAmtINRCurr()[i]));
						pst.setInt(5, promntc_invoice_id);
						pst.setInt(6, uF.parseToInt(getBillDaysHoursINRCurr()[i]));
						pst.setDouble(7, uF.parseToDouble(getStrEmpAmtOtherCurr()[i]));
						pst.execute();
						pst.close();
					}
				}
			}
			
			
			double dblTotOpe = 0;
			double dblTotOpeOtherCurr = 0;
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				for(int i=0; getStrOtherParticularsProrata()!=null && i<getStrOtherParticularsProrata().length; i++) {
					pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount," +
						"promntc_invoice_id,oc_invoice_particulars_amount,head_type) values(?,?,?,?, ?)");
					pst.setString(1, getStrOtherParticularsProrata()[i]);
					pst.setDouble(2, uF.parseToDouble(getStrOtherParticularsAmtProrata()[i]));
					pst.setInt(3, promntc_invoice_id);
					pst.setDouble(4, uF.parseToDouble(getStrOtherParticularsAmtProrata()[i]));
					pst.setString(5, HEAD_OPE);
					pst.executeUpdate();
					pst.close();
					dblTotOpe += uF.parseToDouble(getStrOtherParticularsAmtProrata()[i]);
					dblTotOpeOtherCurr += uF.parseToDouble(getStrOtherParticularsAmtProrata()[i]);
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0; getStrOtherParticularsINRCurrProrata()!=null && i<getStrOtherParticularsINRCurrProrata().length; i++) {
				pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount," +
					"promntc_invoice_id,oc_invoice_particulars_amount,head_type) values(?,?,?,?, ?)");
				pst.setString(1, getStrOtherParticularsINRCurrProrata()[i]);
				pst.setDouble(2, uF.parseToDouble(getStrOtherParticularsAmtINRCurrProrata()[i]));
				pst.setInt(3, promntc_invoice_id);
				pst.setDouble(4, uF.parseToDouble(getStrOtherParticularsAmtOtherCurrProrata()[i]));
				pst.setString(5, HEAD_OPE);
				pst.executeUpdate();
				pst.close();
				dblTotOpe += uF.parseToDouble(getStrOtherParticularsAmtINRCurrProrata()[i]);
				dblTotOpeOtherCurr += uF.parseToDouble(getStrOtherParticularsAmtOtherCurrProrata()[i]);
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
						"oc_invoice_particulars_amount,tax_percent,head_type) values(?,?,?,?, ?,?)");
					pst.setString(1, uF.showData(getTaxHead()[i], ""));
					pst.setDouble(2, uF.parseToDouble(getTaxHeadAmt()[i]));
					pst.setInt(3, promntc_invoice_id);
					pst.setDouble(4, uF.parseToDouble(getTaxHeadAmt()[i]));
					pst.setDouble(5, uF.parseToDouble(getTaxHeadPercent()[i]));
					pst.setString(6, HEAD_TAX);
					pst.executeUpdate();
					pst.close();
				}
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				for(int i=0; getTaxHeadOtherCurr()!=null && i<getTaxHeadOtherCurr().length; i++) {
					pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars, invoice_particulars_amount, promntc_invoice_id," +
						"oc_invoice_particulars_amount,tax_percent,head_type) values(?,?,?,?, ?,?)");
					pst.setString(1, uF.showData(getTaxHeadOtherCurr()[i], ""));
					pst.setDouble(2, uF.parseToDouble(getTaxHeadAmtINRCurr()[i]));
					pst.setInt(3, promntc_invoice_id);
					pst.setDouble(4, uF.parseToDouble(getTaxHeadAmtOtherCurr()[i]));
					pst.setDouble(5, uF.parseToDouble(getTaxHeadPercentOtherCurr()[i]));
					pst.setString(6, HEAD_TAX);
					pst.executeUpdate();
					pst.close();
				}
			}
			
			
			/**
			 * Alerts
			 * */
			String strDomain = request.getServerName().split("\\.")[0];
			
			String proName = CF.getProjectNameById(con, getPro_id());
			StringBuilder alertData = new StringBuilder();
			alertData.append("<div style=\"float: left;\"> <b>"+getInvoiceCode()+"</b> new adhoc invoice has been generated");
			if(uF.parseToInt(getPro_id()) > 0) {
				alertData.append(" for <b>"+proName+"</b> project");
			}
			alertData.append(" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>");
			String alertAction = "Billing.action";
			
			if(uF.parseToInt(getStrProjectOwner()) > 0){
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(getStrProjectOwner());
				userAlerts.setStrData(alertData.toString());
				userAlerts.setStrAction(alertAction);
//				userAlerts.set_type(INVOICE_GENERATED_ALERT);
				userAlerts.setStatus(INSERT_TR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
				StringBuilder activityData = new StringBuilder();
				activityData.append("<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> I </span> <b>"+getInvoiceCode()+"</b> new adhoc invoice has been generated");
				if(uF.parseToInt(getPro_id()) > 0) {
					activityData.append(" for <b>"+proName+"</b> project");
				}
				activityData.append(" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>");
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
				userAlerts.setStrData(alertData.toString());
				userAlerts.setStrAction(alertAction);
//				userAlerts.set_type(INVOICE_GENERATED_ALERT);
				userAlerts.setStatus(INSERT_TR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
				StringBuilder activityData = new StringBuilder();
				activityData.append("<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> I </span> <b>"+getInvoiceCode()+"</b> new adhoc invoice has been generated");
				if(uF.parseToInt(getPro_id()) > 0) {
					activityData.append(" for <b>"+proName+"</b> project");
				}
				activityData.append(" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>");
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
			
			
			Notifications nF = new Notifications(N_PAYMENT_ALERT, CF);
			nF.setDomain(strDomain);
			
			nF.request = request;
			nF.setStrOrgId((String)session.getAttribute(ORGID));
			nF.setEmailTemplate(true);

			nF.setStrEmpId(strSessionEmpId);
			pst = con.prepareStatement("select * from client_poc where poc_id = ?");
			if(uF.parseToInt(getClientPoc()) > 0) {
				pst.setInt(1, uF.parseToInt(getClientPoc()));
			} else {
				pst.setInt(1, nClientPocId);
			}
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
				nF.setStrInvoiceNo(getInvoiceCode());
				nF.sendNotifications(); 
			}
			
			
//			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
//				pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//					"oc_invoice_particulars_amount) values(?,?,?,?)");
//				pst.setString(1, "STAX");
//				pst.setDouble(2, uF.parseToDouble(getServiceTaxAmt()));
//				pst.setInt(3, promntc_invoice_id);
//				pst.setDouble(4, uF.parseToDouble(getServiceTaxAmt()));
//				pst.execute();
//				pst.close();
//				
//				pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//					"oc_invoice_particulars_amount) values(?,?,?,?)");
//				pst.setString(1, "EDUCESS");
//				pst.setDouble(2, uF.parseToDouble(getEduCessAmt()));
//				pst.setInt(3, promntc_invoice_id);
//				pst.setDouble(4, uF.parseToDouble(getEduCessAmt()));
//				pst.execute();
//				pst.close();
//				
//				pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//					"oc_invoice_particulars_amount) values(?,?,?,?)");
//				pst.setString(1, "SHSCESS");
//				pst.setDouble(2, uF.parseToDouble(getStdTaxAmt()));
//				pst.setInt(3, promntc_invoice_id);
//				pst.setDouble(4, uF.parseToDouble(getStdTaxAmt()));
//				pst.execute();
//				pst.close();
//			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
	
	
	private void insertProjectAdHocInvoice(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			//Map<String,String> hmProjectDetails=(Map<String,String>)request.getAttribute("hmProjectDetails");
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			Map<String, String> hmLocation = hmWorkLocation.get(hmEmpWlocationMap.get(getStrProjectOwner()));
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
			Map<String, String> hmDept = CF.getDeptMap(con);
//			Map<String,String> hmTaxMiscSetting = (Map<String,String>)request.getAttribute("hmTaxMiscSetting");
			
			String[] arr = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
//			String locationCode=hmLocation.get("WL_NAME").substring(0,3);
			String locationCode = hmLocation.get("WL_NAME");
			String departCode = "";
			if(hmDept.get(hmEmpDepartment.get(getStrProjectOwner()))!=null && hmDept.get(hmEmpDepartment.get(getStrProjectOwner())).contains(" ")) {
				String[] temp = hmDept.get(hmEmpDepartment.get(getStrProjectOwner())).toUpperCase().split(" ");
				for(int i=0;i<temp.length;i++) {
					departCode += temp[i].substring(0,1);
				}
			} else if(hmDept.get(hmEmpDepartment.get(getStrProjectOwner()))!=null) {
				departCode = hmDept.get(hmEmpDepartment.get(getStrProjectOwner())).length() > 3 ? hmDept.get(hmEmpDepartment.get(getStrProjectOwner())).substring(0,3) : hmDept.get(hmEmpDepartment.get(getStrProjectOwner()));
			}
			
			int count = 0;
//			pst=con.prepareStatement("select count(promntc_invoice_id) as count from promntc_invoice_details where wlocation_id=? and depart_id=?");
			pst=con.prepareStatement("select max(invoice_no) as invoice_no from promntc_invoice_details where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(hmEmpWlocationMap.get(getStrProjectOwner())));
//			pst.setInt(2, uF.parseToInt(hmEmpDepartment.get(getStrProjectOwner())));
			rs=pst.executeQuery();
			while(rs.next()) {
				count=rs.getInt("invoice_no");
			}
			rs.close();
			pst.close();
			count++;
			
			String invoiceCode = count +"-" + uF.getDateFormat(arr[0], DATE_FORMAT, "yyyy") + "-" + uF.getDateFormat(arr[1], DATE_FORMAT, "yy") + "/" + locationCode.toUpperCase(); // + "/" + departCode.toUpperCase()
			
			int nClientId = 0;
			int nClientPocId = 0;
			int nClientAddressId = 0;
			int nProId = 0;
			if(getOthrClientName() != null && !getOthrClientName().equals("")) {
				pst = con.prepareStatement("insert into client_details (client_name, client_type, client_industry) values (?,?,?)");
				pst.setString(1, getOthrClientName());
				pst.setInt(2, CLIENT_TYPE_OTHER);
				pst.setString(3, "");
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(client_id) as client_id from client_details");
				rs = pst.executeQuery();
				while(rs.next()) {
					nClientId = rs.getInt("client_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("insert into client_poc (contact_fname, contact_email, contact_number, contact_desig, contact_department, client_id) values (?,?,?,?,?,?)");
				pst.setString(1, uF.showData(getOtheClientPoc(),"N/A"));
				pst.setString(2, "N/A"); 
				pst.setString(3, "N/A"); 
				pst.setString(4, "N/A"); 
				pst.setString(5, "N/A"); 
				pst.setInt(6, nClientId);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(poc_id) as poc_id from client_poc");
				rs = pst.executeQuery();
				while(rs.next()) {
					nClientPocId = rs.getInt("poc_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("insert into client_address (client_address, client_id) values (?,?)");
				pst.setString(1, getOtherClientAddress()); 
				pst.setInt(2, nClientId);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(client_address_id) as client_address_id from client_address");
				rs = pst.executeQuery();
				while(rs.next()) {
					nClientAddressId = rs.getInt("client_address_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("insert into projectmntnc(pro_name,client_id,curr_id,billing_curr_id,added_by,project_type) values(?,?,?,?, ?,?)");
				pst.setString(1, getOthrClntProject());
				pst.setInt(2, nClientId);
				pst.setInt(3, uF.parseToInt(getStrCurrency()));
				pst.setInt(4, uF.parseToInt(getStrCurrency()));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setInt(6, PROJECT_TYPE_OTHER);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(pro_id) as pro_id from projectmntnc");
				rs = pst.executeQuery();
				while(rs.next()) {
					nProId = rs.getInt("pro_id");
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("insert into promntc_invoice_details(invoice_generated_date,invoice_generated_by,invoice_from_date," +
				"invoice_to_date,pro_id,invoice_code,project_description,other_description,spoc_id,address_id,pro_owner_id,financial_start_date," +
				"financial_end_date,wlocation_id,depart_id,invoice_amount,particulars_total_amount,curr_id,other_amount,other_particular," +
				"bank_branch_id,invoice_type,client_id,service_id,standard_tax,education_tax,service_tax,reference_no_desc,oc_particulars_total_amount," +
				"oc_invoice_amount,oc_other_amount,adhoc_billing_type,pro_freq_id,entry_date,invoice_template_id)" +
				"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setDate(1, uF.getDateFormat(getInvoiceGenDate(), DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				pst.setDate(3, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				pst.setDate(3, uF.getDateFormat(getStrStartDateOtherCurr(), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(getStrEndDateOtherCurr(), DATE_FORMAT));
			}
			if(uF.parseToInt(getPro_id()) > 0) {
				pst.setInt(5, uF.parseToInt(getPro_id()));
			} else {
				pst.setInt(5, nProId);
			}
			pst.setString(6, invoiceCode);
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				pst.setString(7, getProDescription());
				pst.setString(8, getOtherDescription());
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				pst.setString(7, getProDescriptionOtherCurr());
				pst.setString(8, getOtherDescriptionOtherCurr());
			}
			if(uF.parseToInt(getClientPoc()) > 0) {
				pst.setInt(9, uF.parseToInt(getClientPoc()));
			} else {
				pst.setInt(9, nClientPocId);
			}
			if(uF.parseToInt(getClientAddress()) > 0) {
				pst.setInt(10, uF.parseToInt(getClientAddress()));
			} else {
				pst.setInt(10, nClientAddressId);
			}
			pst.setInt(11, uF.parseToInt(getStrProjectOwner()));
			pst.setDate(12, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(13, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setInt(14, uF.parseToInt(hmEmpWlocationMap.get(getStrProjectOwner())));
			pst.setInt(15, uF.parseToInt(hmEmpDepartment.get(getStrProjectOwner()))); 
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				pst.setDouble(16, uF.parseToDouble(getTotalAmt()));
				pst.setDouble(17, uF.parseToDouble(getParticularTotalAmt()));
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				pst.setDouble(16, uF.parseToDouble(getTotalAmtINRCurr()));
				pst.setDouble(17, uF.parseToDouble(getParticularTotalAmtINRCurr()));
			}
			pst.setInt(18, uF.parseToInt(getStrCurrency()));
//			pst.setDouble(19, uF.parseToDouble(getStrOtherParticularsAmt()));
//			pst.setString(20, getStrOtherParticulars());
			pst.setDouble(19, 0);
			pst.setString(20, "");
			pst.setInt(21, uF.parseToInt(getBankName())); 
			pst.setInt(22, ADHOC_INVOICE); 
			if(uF.parseToInt(getClient()) > 0) {
				pst.setInt(23, uF.parseToInt(getClient()));
			} else {
				pst.setInt(23, nClientId);
			}
			pst.setInt(24, uF.parseToInt(getService())); 
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
				pst.setDouble(31, 0);
//				pst.setDouble(30, uF.parseToDouble(getStrOtherParticularsAmtOtherCurr()));
			}
			pst.setInt(32, uF.parseToInt(getBillingType()));
			pst.setInt(33, 0);
			pst.setDate(34, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(35, 0);
			int x=pst.executeUpdate();
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
				
				if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
					for(int i=0;getStrParticulars()!=null && i<getStrParticulars().length;i++) {
						/*if(!getStrParticulars()[i].equals("")) {*/
							pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount," +
								"promntc_invoice_id,oc_invoice_particulars_amount,head_type) values(?,?,?,?, ?)");
							pst.setString(1, getStrParticulars()[i]);
							pst.setDouble(2, uF.parseToDouble(getStrParticularsAmt()[i]));
							pst.setInt(3, promntc_invoice_id);
							pst.setDouble(4, uF.parseToDouble(getStrParticularsAmt()[i]));
							pst.setString(5, HEAD_PARTI);
							pst.executeUpdate();
							pst.close();
						/*}*/
					}
				} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
					for(int i=0;getStrParticularsINRCurr()!=null && i<getStrParticularsINRCurr().length;i++) {
						/*if(!getStrParticularsINRCurr()[i].equals("")) {*/
							pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount," +
								"promntc_invoice_id,oc_invoice_particulars_amount,head_type) values(?,?,?,?, ?)");
							pst.setString(1, getStrParticularsINRCurr()[i]);
							pst.setDouble(2, uF.parseToDouble(getStrParticularsAmtINRCurr()[i]));
							pst.setInt(3, promntc_invoice_id);
							pst.setDouble(4, uF.parseToDouble(getStrParticularsAmtOtherCurr()[i]));
							pst.setString(5, HEAD_PARTI);
							pst.executeUpdate();
							pst.close();
						/*}*/
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
				
				
				/**
				 * Alerts
				 * */
				String strDomain = request.getServerName().split("\\.")[0];
				
				String proName = CF.getProjectNameById(con, getPro_id());
				StringBuilder alertData = new StringBuilder();
				alertData.append("<div style=\"float: left;\"> <b>"+getInvoiceCode()+"</b> new adhoc invoice has been generated");
				if(uF.parseToInt(getPro_id()) > 0) {
					alertData.append(" for <b>"+proName+"</b> project");
				}
				alertData.append(" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>");
				String alertAction = "Billing.action";
				
				if(uF.parseToInt(getStrProjectOwner()) > 0){
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(getStrProjectOwner());
					userAlerts.setStrData(alertData.toString());
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(INVOICE_GENERATED_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					StringBuilder activityData = new StringBuilder();
					activityData.append("<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> I </span> <b>"+getInvoiceCode()+"</b> new adhoc invoice has been generated");
					if(uF.parseToInt(getPro_id()) > 0) {
						activityData.append(" for <b>"+proName+"</b> project");
					}
					activityData.append(" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>");
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
					userAlerts.setStrData(alertData.toString());
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(INVOICE_GENERATED_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					StringBuilder activityData = new StringBuilder();
					activityData.append("<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> I </span> <b>"+getInvoiceCode()+"</b> new adhoc invoice has been generated");
					if(uF.parseToInt(getPro_id()) > 0) {
						activityData.append(" for <b>"+proName+"</b> project");
					}
					activityData.append(" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>");
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
				
				
				Notifications nF = new Notifications(N_PAYMENT_ALERT, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);
				
				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				if(uF.parseToInt(getClientPoc()) > 0) {
					pst.setInt(1, uF.parseToInt(getClientPoc()));
				} else {
					pst.setInt(1, nClientPocId);
				}
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
					nF.setStrInvoiceNo(getInvoiceCode());
					nF.sendNotifications(); 
				}
				
//				if(getStrCurrency() != null && getStrCurrency().equals("3")) {
//					pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//						"oc_invoice_particulars_amount) values(?,?,?,?)");
//					pst.setString(1, "STAX");
//					pst.setDouble(2, uF.parseToDouble(getServiceTaxAmt()));
//					pst.setInt(3, promntc_invoice_id);
//					pst.setDouble(4, uF.parseToDouble(getServiceTaxAmt()));
//					pst.executeUpdate();
//					pst.close();
//					
//					pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//						"oc_invoice_particulars_amount) values(?,?,?,?)");
//					pst.setString(1, "EDUCESS");
//					pst.setDouble(2, uF.parseToDouble(getEduCessAmt()));
//					pst.setInt(3, promntc_invoice_id);
//					pst.setDouble(4, uF.parseToDouble(getEduCessAmt()));
//					pst.executeUpdate();
//					pst.close();
//					
//					pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//						"oc_invoice_particulars_amount) values(?,?,?,?)");
//					pst.setString(1, "SHSCESS");
//					pst.setDouble(2, uF.parseToDouble(getStdTaxAmt()));
//					pst.setInt(3, promntc_invoice_id);
//					pst.setDouble(4, uF.parseToDouble(getStdTaxAmt()));
//					pst.executeUpdate();
//					pst.close();
//				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getProjectAdHocDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			loadProjectAdHocInvoice(uF, con);
			
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
			
			if(getStrCurrency() == null) {
				setStrCurrency(INR_CURR_ID);
			}
			
			pst = con.prepareStatement("select * from billing_head_setting where org_id=? and (head_data_type="+DT_OPE+" or head_data_type="+DT_OPE_OVERALL+") order by billing_head_id");
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmProRataProBillingHeadData = new LinkedHashMap<String, List<String>>();
			int strProRaraPerticnt = 0;
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("billing_head_id"));
				alInner.add(rs.getString("head_label"));
				alInner.add(rs.getString("head_data_type"));
				alInner.add(rs.getString("head_other_variable"));
				if(uF.parseToInt(rs.getString("head_data_type")) != DT_OPE && uF.parseToInt(rs.getString("head_data_type")) != DT_OPE_OVERALL) {
					strProRaraPerticnt++;
				}
				hmProRataProBillingHeadData.put(rs.getString("billing_head_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProRataProBillingHeadData", hmProRataProBillingHeadData);
			request.setAttribute("strProRaraPerticnt", strProRaraPerticnt+"");
			
			
			pst = con.prepareStatement("select * from billing_head_setting where org_id=? order by billing_head_id");
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmProBillingHeadData = new LinkedHashMap<String, List<String>>();
			int strPerticnt = 0;
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("billing_head_id"));
				alInner.add(rs.getString("head_label"));
				alInner.add(rs.getString("head_data_type"));
				alInner.add(rs.getString("head_other_variable"));
				if(uF.parseToInt(rs.getString("head_data_type")) != DT_OPE && uF.parseToInt(rs.getString("head_data_type")) != DT_OPE_OVERALL) {
					strPerticnt++;
				}
				hmProBillingHeadData.put(rs.getString("billing_head_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProBillingHeadData", hmProBillingHeadData);
			request.setAttribute("strPerticnt", strPerticnt+"");
			
			
			
			pst = con.prepareStatement("select * from tax_setting where org_id=? and invoice_or_customer=1 order by tax_setting_id");
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmProTaxHeadData = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("tax_setting_id"));
				alInner.add(rs.getString("tax_name_label"));
				alInner.add(rs.getString("tax_percent"));
				alInner.add(rs.getString("tax_name"));
				hmProTaxHeadData.put(rs.getString("tax_setting_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProTaxHeadData", hmProTaxHeadData);
			
			
			
//			pst = con.prepareStatement("select * from deduction_tax_misc_details");
//			rs = pst.executeQuery();
//			Map<String,String> hmTaxMiscSetting=new HashMap<String, String>();
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
			
//			request.setAttribute("hmClientDetails", hmClientDetails);
//			request.setAttribute("hmProjectDetails", hmProjectDetails);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			request.setAttribute("hmCurrencyDetails", hmCurrencyDetails);
			
			
			
//	 		=============================== Invoice Code Generation ======================================
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);			
			Map<String, Map<String, String>> hmWorkLocation =CF.getWorkLocationMap(con);
			Map<String, String> hmLocation=hmWorkLocation.get(hmEmpWlocationMap.get(strSessionEmpId));
			Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);
			Map<String, String> hmDept =CF.getDeptMap(con);
//			Map<String,String> hmTaxMiscSetting=(Map<String,String>)request.getAttribute("hmTaxMiscSetting");
			
			String[] arr=CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
//			String locationCode=hmLocation.get("WL_NAME").substring(0,3);
			String locationCode=hmLocation.get("WL_NAME");
			String departCode="";
			if(hmDept.get(hmEmpDepartment.get(strSessionEmpId))!=null && hmDept.get(hmEmpDepartment.get(strSessionEmpId)).contains(" ")) {
				String[] temp=hmDept.get(hmEmpDepartment.get(strSessionEmpId)).toUpperCase().split(" ");
				for(int i=0;i<temp.length;i++) {
					departCode+=temp[i].substring(0,1);
				}
			} else if(hmDept.get(hmEmpDepartment.get(strSessionEmpId))!=null) {
				
				departCode=hmDept.get(hmEmpDepartment.get(strSessionEmpId)).length() > 3 ? hmDept.get(hmEmpDepartment.get(strSessionEmpId)).substring(0,3) : hmDept.get(hmEmpDepartment.get(strSessionEmpId));
			}
			
			int count=0;
			pst=con.prepareStatement("select max(invoice_no) as invoice_no from promntc_invoice_details where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(hmEmpWlocationMap.get(strSessionEmpId)));
			rs=pst.executeQuery();
			while(rs.next()) {
				count=rs.getInt("invoice_no");
			}
			rs.close();
			pst.close();
			count++;
			
			String invoiceCode = count +"-" + uF.getDateFormat(arr[0], DATE_FORMAT, "yyyy") + "-" + uF.getDateFormat(arr[1], DATE_FORMAT, "yy") + "/" + locationCode.toUpperCase(); // + "/" + departCode.toUpperCase()
			
			if(getInvoiceCode() == null || getInvoiceCode().trim().equals("")) {
				setInvoiceCode(invoiceCode);
			}
// 		========================================================= End =========================================================			
	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void loadProjectAdHocInvoice(UtilityFunctions uF, Connection con) {
//		Map<String,String> hmClientDetails=(Map<String,String>)request.getAttribute("hmClientDetails");
		
		String strClientId = CF.getClientIdByProjectId(con, uF, getPro_id());
		clientPocList = new FillClientPoc(request).fillClientPoc(strClientId);
		clientAddressList =new FillClientAddress(request).fillClientAddress(strClientId, uF); 
		projectOwnerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		//bankList = new FillBank().fillBankDetails();
		bankList = new FillBank(request).fillBankAccNo();
		clientList = new FillClients(request).fillClients(true);
		serviceList = new FillServices(request).fillServices(null, uF);
		currencyList= new FillCurrency(request).fillCurrency();
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

	public String getOthrClientName() {
		return othrClientName;
	}

	public void setOthrClientName(String othrClientName) {
		this.othrClientName = othrClientName;
	}

	public String getOtheClientPoc() {
		return otheClientPoc;
	}

	public void setOtheClientPoc(String otheClientPoc) {
		this.otheClientPoc = otheClientPoc;
	}

	public String getOtherClientAddress() {
		return otherClientAddress;
	}

	public void setOtherClientAddress(String otherClientAddress) {
		this.otherClientAddress = otherClientAddress;
	}

	public String getOthrClntProject() {
		return othrClntProject;
	}

	public void setOthrClntProject(String othrClntProject) {
		this.othrClntProject = othrClntProject;
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

	public String getStrTotalAmt() {
		return strTotalAmt;
	}

	public void setStrTotalAmt(String strTotalAmt) {
		this.strTotalAmt = strTotalAmt;
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

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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

	public String[] getStrParticularsINRCurr() {
		return strParticularsINRCurr;
	}

	public void setStrParticularsINRCurr(String[] strParticularsINRCurr) {
		this.strParticularsINRCurr = strParticularsINRCurr;
	}

	public String[] getStrParticularsAmtINRCurr() {
		return strParticularsAmtINRCurr;
	}

	public void setStrParticularsAmtINRCurr(String[] strParticularsAmtINRCurr) {
		this.strParticularsAmtINRCurr = strParticularsAmtINRCurr;
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

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	public String getStrStartDateProrata() {
		return strStartDateProrata;
	}

	public void setStrStartDateProrata(String strStartDateProrata) {
		this.strStartDateProrata = strStartDateProrata;
	}

	public String getStrEndDateProrata() {
		return strEndDateProrata;
	}

	public void setStrEndDateProrata(String strEndDateProrata) {
		this.strEndDateProrata = strEndDateProrata;
	}

	public String getStrReferenceNoProrata() {
		return strReferenceNoProrata;
	}

	public void setStrReferenceNoProrata(String strReferenceNoProrata) {
		this.strReferenceNoProrata = strReferenceNoProrata;
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

	public String[] getBillDaysHours() {
		return billDaysHours;
	}

	public void setBillDaysHours(String[] billDaysHours) {
		this.billDaysHours = billDaysHours;
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

	public String getProDescriptionProrata() {
		return proDescriptionProrata;
	}

	public void setProDescriptionProrata(String proDescriptionProrata) {
		this.proDescriptionProrata = proDescriptionProrata;
	}

	public String getOtherDescriptionProrata() {
		return otherDescriptionProrata;
	}

	public void setOtherDescriptionProrata(String otherDescriptionProrata) {
		this.otherDescriptionProrata = otherDescriptionProrata;
	}

	public String getStrStartDateOtherCurrProrata() {
		return strStartDateOtherCurrProrata;
	}

	public void setStrStartDateOtherCurrProrata(String strStartDateOtherCurrProrata) {
		this.strStartDateOtherCurrProrata = strStartDateOtherCurrProrata;
	}

	public String getStrEndDateOtherCurrProrata() {
		return strEndDateOtherCurrProrata;
	}

	public void setStrEndDateOtherCurrProrata(String strEndDateOtherCurrProrata) {
		this.strEndDateOtherCurrProrata = strEndDateOtherCurrProrata;
	}

	public String getStrReferenceNoOtherCurrProrata() {
		return strReferenceNoOtherCurrProrata;
	}

	public void setStrReferenceNoOtherCurrProrata(String strReferenceNoOtherCurrProrata) {
		this.strReferenceNoOtherCurrProrata = strReferenceNoOtherCurrProrata;
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

	public String[] getBillDaysHoursINRCurr() {
		return billDaysHoursINRCurr;
	}

	public void setBillDaysHoursINRCurr(String[] billDaysHoursINRCurr) {
		this.billDaysHoursINRCurr = billDaysHoursINRCurr;
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

	public String getProDescriptionOtherCurrProrata() {
		return proDescriptionOtherCurrProrata;
	}

	public void setProDescriptionOtherCurrProrata(String proDescriptionOtherCurrProrata) {
		this.proDescriptionOtherCurrProrata = proDescriptionOtherCurrProrata;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String[] getStrParticularsAmtOtherCurr() {
		return strParticularsAmtOtherCurr;
	}

	public void setStrParticularsAmtOtherCurr(String[] strParticularsAmtOtherCurr) {
		this.strParticularsAmtOtherCurr = strParticularsAmtOtherCurr;
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

	public String getProDescriptionOtherCurr() {
		return proDescriptionOtherCurr;
	}

	public void setProDescriptionOtherCurr(String proDescriptionOtherCurr) {
		this.proDescriptionOtherCurr = proDescriptionOtherCurr;
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

	public String getOtherDescriptionOtherCurr() {
		return otherDescriptionOtherCurr;
	}

	public void setOtherDescriptionOtherCurr(String otherDescriptionOtherCurr) {
		this.otherDescriptionOtherCurr = otherDescriptionOtherCurr;
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

	public String[] getStrOtherParticularsProrata() {
		return strOtherParticularsProrata;
	}

	public void setStrOtherParticularsProrata(String[] strOtherParticularsProrata) {
		this.strOtherParticularsProrata = strOtherParticularsProrata;
	}

	public String[] getStrOtherParticularsAmtProrata() {
		return strOtherParticularsAmtProrata;
	}

	public void setStrOtherParticularsAmtProrata(String[] strOtherParticularsAmtProrata) {
		this.strOtherParticularsAmtProrata = strOtherParticularsAmtProrata;
	}

	public String[] getStrOtherParticularsINRCurrProrata() {
		return strOtherParticularsINRCurrProrata;
	}

	public void setStrOtherParticularsINRCurrProrata(String[] strOtherParticularsINRCurrProrata) {
		this.strOtherParticularsINRCurrProrata = strOtherParticularsINRCurrProrata;
	}

	public String[] getStrOtherParticularsAmtINRCurrProrata() {
		return strOtherParticularsAmtINRCurrProrata;
	}

	public void setStrOtherParticularsAmtINRCurrProrata(String[] strOtherParticularsAmtINRCurrProrata) {
		this.strOtherParticularsAmtINRCurrProrata = strOtherParticularsAmtINRCurrProrata;
	}

	public String[] getStrOtherParticularsAmtOtherCurrProrata() {
		return strOtherParticularsAmtOtherCurrProrata;
	}

	public void setStrOtherParticularsAmtOtherCurrProrata(String[] strOtherParticularsAmtOtherCurrProrata) {
		this.strOtherParticularsAmtOtherCurrProrata = strOtherParticularsAmtOtherCurrProrata;
	}

	public String getOtherDescriptionOtherCurrProrata() {
		return otherDescriptionOtherCurrProrata;
	}

	public void setOtherDescriptionOtherCurrProrata(String otherDescriptionOtherCurrProrata) {
		this.otherDescriptionOtherCurrProrata = otherDescriptionOtherCurrProrata;
	}

	public String getStrTotalAmtINRCurr() {
		return strTotalAmtINRCurr;
	}

	public void setStrTotalAmtINRCurr(String strTotalAmtINRCurr) {
		this.strTotalAmtINRCurr = strTotalAmtINRCurr;
	}

	public String getStrTotalAmtOtherCurr() {
		return strTotalAmtOtherCurr;
	}

	public void setStrTotalAmtOtherCurr(String strTotalAmtOtherCurr) {
		this.strTotalAmtOtherCurr = strTotalAmtOtherCurr;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String[] getParticularId() {
		return particularId;
	}

	public void setParticularId(String[] particularId) {
		this.particularId = particularId;
	}

	public String[] getOtherParticularId() {
		return otherParticularId;
	}

	public void setOtherParticularId(String[] otherParticularId) {
		this.otherParticularId = otherParticularId;
	}

	public String[] getParticularIdOtherCurr() {
		return particularIdOtherCurr;
	}

	public void setParticularIdOtherCurr(String[] particularIdOtherCurr) {
		this.particularIdOtherCurr = particularIdOtherCurr;
	}

	public String[] getOtherParticularIdOtherCurr() {
		return otherParticularIdOtherCurr;
	}

	public void setOtherParticularIdOtherCurr(String[] otherParticularIdOtherCurr) {
		this.otherParticularIdOtherCurr = otherParticularIdOtherCurr;
	}

	public String[] getTaxHeadId() {
		return taxHeadId;
	}

	public void setTaxHeadId(String[] taxHeadId) {
		this.taxHeadId = taxHeadId;
	}

	public String[] getTaxHeadIdOtherCurr() {
		return taxHeadIdOtherCurr;
	}

	public void setTaxHeadIdOtherCurr(String[] taxHeadIdOtherCurr) {
		this.taxHeadIdOtherCurr = taxHeadIdOtherCurr;
	}

	public String getLblClientPoc() {
		return lblClientPoc;
	}

	public void setLblClientPoc(String lblClientPoc) {
		this.lblClientPoc = lblClientPoc;
	}

	public String getLblClientAddress() {
		return lblClientAddress;
	}

	public void setLblClientAddress(String lblClientAddress) {
		this.lblClientAddress = lblClientAddress;
	}

	public String getLblProjectOwner() {
		return lblProjectOwner;
	}

	public void setLblProjectOwner(String lblProjectOwner) {
		this.lblProjectOwner = lblProjectOwner;
	}

	public String getLblCurrency() {
		return lblCurrency;
	}

	public void setLblCurrency(String lblCurrency) {
		this.lblCurrency = lblCurrency;
	}

	public String getLblBankName() {
		return lblBankName;
	}

	public void setLblBankName(String lblBankName) {
		this.lblBankName = lblBankName;
	}

	public String getLblClient() {
		return lblClient;
	}

	public void setLblClient(String lblClient) {
		this.lblClient = lblClient;
	}

	public String getLblService() {
		return lblService;
	}

	public void setLblService(String lblService) {
		this.lblService = lblService;
	}

	public String getLblBillingType() {
		return lblBillingType;
	}

	public void setLblBillingType(String lblBillingType) {
		this.lblBillingType = lblBillingType;
	}

	public String getLblInvoiceCode() {
		return lblInvoiceCode;
	}

	public void setLblInvoiceCode(String lblInvoiceCode) {
		this.lblInvoiceCode = lblInvoiceCode;
	}

	public String getStrTotalAmtProrata() {
		return strTotalAmtProrata;
	}

	public void setStrTotalAmtProrata(String strTotalAmtProrata) {
		this.strTotalAmtProrata = strTotalAmtProrata;
	}

	public String getStrTotalAmtINRCurrProrata() {
		return strTotalAmtINRCurrProrata;
	}

	public void setStrTotalAmtINRCurrProrata(String strTotalAmtINRCurrProrata) {
		this.strTotalAmtINRCurrProrata = strTotalAmtINRCurrProrata;
	}

	public String getStrTotalAmtOtherCurrProrata() {
		return strTotalAmtOtherCurrProrata;
	}

	public void setStrTotalAmtOtherCurrProrata(String strTotalAmtOtherCurrProrata) {
		this.strTotalAmtOtherCurrProrata = strTotalAmtOtherCurrProrata;
	}

	public String[] getResourceId() {
		return resourceId;
	}

	public void setResourceId(String[] resourceId) {
		this.resourceId = resourceId;
	}

	public String[] getResourceIdOtherCurr() {
		return resourceIdOtherCurr;
	}

	public void setResourceIdOtherCurr(String[] resourceIdOtherCurr) {
		this.resourceIdOtherCurr = resourceIdOtherCurr;
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

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

}
