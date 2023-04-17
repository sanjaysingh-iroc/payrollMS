package com.konnect.jpms.task.tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.task.FillBillingHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTaxHead extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	
	String strOrg;
	String taxHead;
	String taxHeadLabel;
	String taxPercent;
	String invoiceOrCustomer;
	String taxHeadId;
	String operation;
	String effectiveDate;
	String btnOk;
	
	List<FillOrganisation> orgList;
	List<FillBillingHeads> taxDeductionTypeList;

	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		orgList = new FillOrganisation(request).fillOrganisation();
		taxDeductionTypeList = new FillBillingHeads().fillTAxDeductionTypeList();
		
		if (operation!=null && operation.equals("D")) {
			return deleteTaxHead(getTaxHeadId(), uF); 
		} 
		if (operation!=null && operation.equals("E")) { 
			return viewTaxHead(getTaxHeadId(), uF);
		}
		if (getTaxHeadId() != null && getTaxHeadId().length()>0) { 
			return updateTaxHead(uF);
		}
		
		if(getTaxHead()!=null && getTaxHead().length()>0){
			return insertTaxHead(uF);
		}
		return LOAD;
	}

	
	public String insertTaxHead(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("INSERT INTO tax_setting(tax_name,tax_percent,org_id,invoice_or_customer,added_by,entry_date,tax_name_label) VALUES (?,?,?,?, ?,?,?)");
			pst.setString(1, getTaxHead());
			pst.setDouble(2, uF.parseToDouble(getTaxPercent()));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(getInvoiceOrCustomer()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(7, getTaxHeadLabel());
			pst.execute();
			pst.close();
			
			int taxId = 0;
			pst = con.prepareStatement("select max(tax_setting_id) as tax_setting_id from tax_setting");
			rs = pst.executeQuery();
			while (rs.next()) {
				taxId = rs.getInt("tax_setting_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO tax_setting_history(tax_setting_id,tax_percent,invoice_or_customer,added_by,entry_date,effective_date) VALUES (?,?,?,?, ?,?)");
			pst.setInt(1, taxId);
			pst.setDouble(2, uF.parseToDouble(getTaxPercent()));
			pst.setInt(3, uF.parseToInt(getInvoiceOrCustomer()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(6, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getTaxHead()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	
	public String viewTaxHead(String strId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from tax_setting where tax_setting_id=?");
			pst.setInt(1, uF.parseToInt(getTaxHeadId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setTaxHead(rs.getString("tax_name"));
				setTaxHeadLabel(rs.getString("tax_name_label"));
				setTaxPercent(rs.getString("tax_percent"));
				setInvoiceOrCustomer(rs.getString("invoice_or_customer"));
				setStrOrg(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_date from tax_setting_history where tax_setting_history_id = (select max(tax_setting_history_id) from tax_setting_history where tax_setting_id=?)");
			pst.setInt(1, uF.parseToInt(getTaxHeadId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
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
		return LOAD;

	}
	
	

	public String updateTaxHead(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update tax_setting set tax_name=?,tax_percent=?,org_id=?,invoice_or_customer=?,updated_by=?,update_date=?," +
				"tax_name_label=? where tax_setting_id=?");
			pst.setString(1, getTaxHead());
			pst.setDouble(2, uF.parseToDouble(getTaxPercent()));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(getInvoiceOrCustomer()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(7, getTaxHeadLabel());
			pst.setInt(8, uF.parseToInt(getTaxHeadId()));
			pst.executeUpdate();
			pst.close();
			
			if(getBtnOk() != null && getBtnOk().equals("Change")) {
				pst = con.prepareStatement("INSERT INTO tax_setting_history(tax_setting_id,tax_percent,invoice_or_customer,added_by,entry_date,effective_date) VALUES (?,?,?,?, ?,?)");
				pst.setInt(1, uF.parseToInt(getTaxHeadId()));
				pst.setDouble(2, uF.parseToDouble(getTaxPercent()));
				pst.setInt(3, uF.parseToInt(getInvoiceOrCustomer()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(6, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.execute();
				pst.close();
			} else {
				
				int taxHistoryId = 0;
				pst = con.prepareStatement("select max(tax_setting_history_id) as tax_setting_history_id from tax_setting_history where tax_setting_id =?");
				pst.setInt(1, uF.parseToInt(getTaxHeadId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					taxHistoryId = rs.getInt("tax_setting_history_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("UPDATE tax_setting_history set tax_percent=?,invoice_or_customer=?,effective_date=? where tax_setting_id=? and tax_setting_history_id=?");
				pst.setDouble(1, uF.parseToDouble(getTaxPercent()));
				pst.setInt(2, uF.parseToInt(getInvoiceOrCustomer()));
				pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(getTaxHeadId()));
				pst.setInt(5, taxHistoryId);
				pst.execute();
				pst.close();
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+getTaxHead()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteTaxHead(String strId,UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from tax_setting_history where tax_setting_id=?");
			pst.setInt(1, uF.parseToInt(getTaxHeadId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from tax_setting where tax_setting_id=?");
			pst.setInt(1, uF.parseToInt(getTaxHeadId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+" Deleted successfully."+END);
			
			//Delete Salary Heads related to the level.
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;

	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getTaxHead() {
		return taxHead;
	}

	public void setTaxHead(String taxHead) {
		this.taxHead = taxHead;
	}

	public String getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(String taxPercent) {
		this.taxPercent = taxPercent;
	}

	public String getInvoiceOrCustomer() {
		return invoiceOrCustomer;
	}

	public void setInvoiceOrCustomer(String invoiceOrCustomer) {
		this.invoiceOrCustomer = invoiceOrCustomer;
	}

	public String getTaxHeadId() {
		return taxHeadId;
	}

	public void setTaxHeadId(String taxHeadId) {
		this.taxHeadId = taxHeadId;
	}

	public List<FillBillingHeads> getTaxDeductionTypeList() {
		return taxDeductionTypeList;
	}

	public void setTaxDeductionTypeList(List<FillBillingHeads> taxDeductionTypeList) {
		this.taxDeductionTypeList = taxDeductionTypeList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getBtnOk() {
		return btnOk;
	}

	public void setBtnOk(String btnOk) {
		this.btnOk = btnOk;
	}

	public String getTaxHeadLabel() {
		return taxHeadLabel;
	}

	public void setTaxHeadLabel(String taxHeadLabel) {
		this.taxHeadLabel = taxHeadLabel;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	public String getUserscreen() {
		return userscreen;
	}


	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}


	public String getNavigationId() {
		return navigationId;
	}


	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}


	public String getToPage() {
		return toPage;
	}


	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}