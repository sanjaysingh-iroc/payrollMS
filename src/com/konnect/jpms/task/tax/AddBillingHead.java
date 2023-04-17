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

public class AddBillingHead extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	
	String strOrg;
	String billingHead;
	String billingHeadDataType;
	String billingHeadOtherVariable1;
	String billingHeadId;
	String operation;
	
	List<FillOrganisation> orgList;
	List<FillBillingHeads> billingHeadDataTypeList;
	List<FillBillingHeads> billingHeadOtherVariableList;
	
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
		billingHeadDataTypeList = new FillBillingHeads().fillBillingHeadDataTypeList();
		billingHeadOtherVariableList = new FillBillingHeads().fillBillingHeadOtherVariableList();
		
		if (operation!=null && operation.equals("D")) {
			return deleteBillingHead(getBillingHeadId(), uF); 
		} 
		if (operation!=null && operation.equals("E")) { 
			return viewBillingHead(getBillingHeadId(), uF);
		}
		if (getBillingHeadId() != null && getBillingHeadId().length()>0) { 
			return updateBillingHead(uF);
		}
		
		if(getBillingHead()!=null && getBillingHead().length()>0){
			return insertBillingHead(uF);
		}
		return LOAD;
	}

	public String insertBillingHead(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("INSERT INTO billing_head_setting(head_label,head_data_type,head_other_variable,org_id,added_by,entry_date) VALUES (?,?,?,?, ?,?)");
			pst.setString(1, getBillingHead());
			pst.setInt(2, uF.parseToInt(getBillingHeadDataType()));
			if(uF.parseToInt(getBillingHeadDataType()) == DT_PRORATA_INDIVIDUAL || uF.parseToInt(getBillingHeadDataType()) == DT_OPE_INDIVIDUAL) {
				pst.setInt(3, uF.parseToInt(getBillingHeadOtherVariable1()));
			} else {
				pst.setInt(3, 0);
			}
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getBillingHead()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	
	public String viewBillingHead(String strId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from billing_head_setting where billing_head_id=?");
			pst.setInt(1, uF.parseToInt(getBillingHeadId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setBillingHead(rs.getString("head_label"));
				setBillingHeadDataType(rs.getString("head_data_type"));
				setBillingHeadOtherVariable1(rs.getString("head_other_variable"));
				setStrOrg(rs.getString("org_id"));
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
	
	

	public String updateBillingHead(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update billing_head_setting set head_label=?,head_data_type=?,head_other_variable=?,org_id=?,updated_by=?,update_date=? where billing_head_id=?");
			pst.setString(1, getBillingHead());
			pst.setInt(2, uF.parseToInt(getBillingHeadDataType()));
			if(uF.parseToInt(getBillingHeadDataType()) == DT_PRORATA_INDIVIDUAL || uF.parseToInt(getBillingHeadDataType()) == DT_OPE_INDIVIDUAL) {
				pst.setInt(3, uF.parseToInt(getBillingHeadOtherVariable1()));
			} else {
				pst.setInt(3, 0);
			}
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt(getBillingHeadId()));
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+getBillingHead()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		if("LeaveTypeReport.action".equalsIgnoreCase(request.getParameter("URI"))){
//			return "success_redirect";
//		}
		return SUCCESS;

	}
	
	public String deleteBillingHead(String strId,UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from billing_head_setting where billing_head_id=?");
			pst.setInt(1, uF.parseToInt(getBillingHeadId()));
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
		
//		if("LeaveTypeReport.action".equalsIgnoreCase(request.getParameter("URI"))){
//			return "success_redirect";
//		}
		return SUCCESS;

	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getBillingHead() {
		return billingHead;
	}

	public void setBillingHead(String billingHead) {
		this.billingHead = billingHead;
	}

	public String getBillingHeadDataType() {
		return billingHeadDataType;
	}

	public void setBillingHeadDataType(String billingHeadDataType) {
		this.billingHeadDataType = billingHeadDataType;
	}

	public String getBillingHeadOtherVariable1() {
		return billingHeadOtherVariable1;
	}

	public void setBillingHeadOtherVariable1(String billingHeadOtherVariable1) {
		this.billingHeadOtherVariable1 = billingHeadOtherVariable1;
	}

	public String getBillingHeadId() {
		return billingHeadId;
	}

	public void setBillingHeadId(String billingHeadId) {
		this.billingHeadId = billingHeadId;
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

	public List<FillBillingHeads> getBillingHeadDataTypeList() {
		return billingHeadDataTypeList;
	}

	public void setBillingHeadDataTypeList(List<FillBillingHeads> billingHeadDataTypeList) {
		this.billingHeadDataTypeList = billingHeadDataTypeList;
	}

	public List<FillBillingHeads> getBillingHeadOtherVariableList() {
		return billingHeadOtherVariableList;
	}

	public void setBillingHeadOtherVariableList(List<FillBillingHeads> billingHeadOtherVariableList) {
		this.billingHeadOtherVariableList = billingHeadOtherVariableList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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