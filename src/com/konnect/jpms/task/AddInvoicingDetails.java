package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddInvoicingDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId = null;
	
	List<FillClients> clientList;
	List<FillProjectList> projectdetailslist;
	
	String strClient;
	String invoiceGenDate;
	String invoiceCode;
	String invoiceNo;
	String invoiceTotalAmt;
	String totalProfFees;
	String totalOPE;
	String strCGST;
	String strSGST;
	String strIGST;
	String strCGSTAmt;
	String strSGSTAmt;
	String strIGSTAmt;
	String[] p_id;
	String[] proId;
	String[] proFees;
	String[] strOPE;
	
	String submit;
	String operation;
	
	String strStartDate;
	String strEndDate;
 	
	public String execute() throws Exception {
		
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/task/AddInvoicingDetails.jsp");
		request.setAttribute(TITLE, "Add Invoicing Details");
		
		clientList = new FillClients(request).fillClients(false);
		projectdetailslist = new FillProjectList(request).fillProjectDetailsByCustomer(uF.parseToInt(getStrClient()));
		
		getProInvoiceDetails(uF);
		
		if((getSubmit() != null && getSubmit().equals("Submit")) || (getOperation() != null && getOperation().equals("A"))){
			insertProjectInvoice(uF);
			return SUCCESS;
		}
		
		return LOAD;
	}
	
	public void getProInvoiceDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			
			/*if(getStrStartDate()==null && getStrEndDate()==null) {
				
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
				
			}
*/			
			Map<String, Map<String, String>> hmProjectData = new HashMap<String, Map<String, String>>();
			
			pst = con.prepareStatement("select * from projectmntnc where approve_status!= 'blocked'");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_NAME", rs.getString("pro_name"));
				hmInner.put("PRO_CUST_SPOC_ID", rs.getString("poc"));
				hmInner.put("PRO_OWNER_ID", rs.getString("project_owner"));
				hmInner.put("PRO_BILLING_TYPE", CF.getBillinType(rs.getString("billing_type")));
				hmInner.put("PRO_BILL_TYPE", rs.getString("billing_type"));
				hmInner.put("PRO_BILLING_ACTUAL_TYPE", rs.getString("actual_calculation_type"));
				hmInner.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
				hmInner.put("PRO_BILL_FREQUENCY", rs.getString("billing_kind"));
				hmInner.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmInner.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmInner.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmInner.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				hmInner.put("PRO_SERVICE_ID", rs.getString("service"));
				hmInner.put("PRO_REPORT_CURR_ID", rs.getString("curr_id"));
				hmInner.put("PRO_BILLING_CURR_ID", rs.getString("billing_curr_id"));
				hmInner.put("PRO_BILLING_AMOUNT", rs.getString("billing_amount"));
				hmInner.put("PRO_ORG_ID", rs.getString("org_id"));
				hmProjectData.put(rs.getString("pro_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmProjectData", hmProjectData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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
			
			Map<String,Map<String,String>> hmProjectData=(Map<String,Map<String,String>>)request.getAttribute("hmProjectData");
			
			
			if(getProId()!=null && getProId().length != 0){
				
//				String[] arr=CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				
				if(getInvoiceGenDate() == null){
					setInvoiceGenDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT));
				}
				String[] arr=CF.getFinancialYear(con, getInvoiceGenDate(), CF, uF);
				String minMaxDate = uF.getCurrentMonthMinMaxDate(getInvoiceGenDate(), DATE_FORMAT);
				
				String[] tmpDate = minMaxDate.split("::::");
								
				StringBuilder proIds = new StringBuilder();
				proIds.append(",");
				for(int i=0; i<getProId().length; i++){
					proIds.append(getProId()[i]+",");
				}
				
				pst = con.prepareStatement("insert into promntc_invoice_details(invoice_generated_date,invoice_generated_by,invoice_from_date,invoice_to_date,financial_start_date,financial_end_date," +
						"invoice_amount,particulars_total_amount,oc_invoice_amount,oc_particulars_total_amount,client_id,entry_date,invoice_code," +
						"pro_ids,sgst_percent,sgst_amount,cgst_percent,cgst_amount,igst_percent,igst_amount)" +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(getInvoiceGenDate(), DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getDateFormat(tmpDate[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(tmpDate[1], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(arr[0], DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(arr[1], DATE_FORMAT));
				pst.setDouble(7, uF.parseToDouble(getInvoiceTotalAmt()));
				pst.setDouble(8, uF.parseToDouble(getTotalProfFees())+uF.parseToDouble(getTotalOPE()));
				pst.setDouble(9, uF.parseToDouble(getInvoiceTotalAmt()));
				pst.setDouble(10, uF.parseToDouble(getTotalProfFees())+uF.parseToDouble(getTotalOPE()));
				pst.setInt(11, uF.parseToInt(getStrClient()));
				pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(13, getInvoiceNo());
				pst.setString(14, proIds.toString());
				pst.setDouble(15, uF.parseToDouble(getStrSGST()));
				pst.setDouble(16, uF.parseToDouble(getStrSGSTAmt()));
				pst.setDouble(17, uF.parseToDouble(getStrCGST()));
				pst.setDouble(18, uF.parseToDouble(getStrCGSTAmt()));
				pst.setDouble(19, uF.parseToDouble(getStrIGST()));
				pst.setDouble(20, uF.parseToDouble(getStrIGSTAmt()));
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
					
					for(int i=0; i<getProId().length; i++){
						pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
										"oc_invoice_particulars_amount,pro_id,head_type) values(?,?,?,?, ?,?)");
						pst.setString(1, PROFESSIONAL_FEES);
						pst.setDouble(2, uF.parseToDouble(getProFees()[i]));
						pst.setInt(3, promntc_invoice_id);
						pst.setDouble(4, uF.parseToDouble(getProFees()[i]));
						pst.setInt(5, uF.parseToInt(getProId()[i]));
						pst.setString(6, HEAD_PARTI);
						pst.executeUpdate();
						pst.close();
						
						pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
										"oc_invoice_particulars_amount,pro_id,head_type) values(?,?,?,?, ?,?)");
						pst.setString(1, OUT_OF_POCKET_EXPENSES);
						pst.setDouble(2, uF.parseToDouble(getStrOPE()[i]));
						pst.setInt(3, promntc_invoice_id);
						pst.setDouble(4, uF.parseToDouble(getStrOPE()[i]));
						pst.setInt(5, uF.parseToInt(getProId()[i]));
						pst.setString(6, HEAD_OPE);
						pst.executeUpdate();
						pst.close();
					}
					
				}
				
			}
			request.setAttribute(MESSAGE, SUCCESSM+ "Invoice generated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public List<FillClients> getClientList() {
		return clientList;
	}
	
	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}
	
	public String getStrClient() {
		return strClient;
	}
	
	public void setStrClient(String strClient) {
		this.strClient = strClient;
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

	public String getInvoiceTotalAmt() {
		return invoiceTotalAmt;
	}

	public void setInvoiceTotalAmt(String invoiceTotalAmt) {
		this.invoiceTotalAmt = invoiceTotalAmt;
	}

	public String getTotalProfFees() {
		return totalProfFees;
	}

	public void setTotalProfFees(String totalProfFees) {
		this.totalProfFees = totalProfFees;
	}

	public String getTotalOPE() {
		return totalOPE;
	}

	public void setTotalOPE(String totalOPE) {
		this.totalOPE = totalOPE;
	}

	public String getStrCGST() {
		return strCGST;
	}

	public void setStrCGST(String strCGST) {
		this.strCGST = strCGST;
	}

	public String getStrSGST() {
		return strSGST;
	}

	public void setStrSGST(String strSGST) {
		this.strSGST = strSGST;
	}

	public String getStrIGST() {
		return strIGST;
	}

	public void setStrIGST(String strIGST) {
		this.strIGST = strIGST;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String[] getP_id() {
		return p_id;
	}

	public void setP_id(String[] p_id) {
		this.p_id = p_id;
	}

	public String[] getProId() {
		return proId;
	}

	public void setProId(String[] proId) {
		this.proId = proId;
	}

	public String[] getProFees() {
		return proFees;
	}

	public void setProFees(String[] proFees) {
		this.proFees = proFees;
	}

	public String[] getStrOPE() {
		return strOPE;
	}

	public void setStrOPE(String[] strOPE) {
		this.strOPE = strOPE;
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

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getStrCGSTAmt() {
		return strCGSTAmt;
	}

	public void setStrCGSTAmt(String strCGSTAmt) {
		this.strCGSTAmt = strCGSTAmt;
	}

	public String getStrSGSTAmt() {
		return strSGSTAmt;
	}

	public void setStrSGSTAmt(String strSGSTAmt) {
		this.strSGSTAmt = strSGSTAmt;
	}

	public String getStrIGSTAmt() {
		return strIGSTAmt;
	}

	public void setStrIGSTAmt(String strIGSTAmt) {
		this.strIGSTAmt = strIGSTAmt;
	}
	
}
