package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPaySlipFormat;
import com.konnect.jpms.select.FillSalaryCalculationTypes;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class AddPaySlipSetting extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	List<FillPaySlipFormat> paySlipFormatList;
	
	CommonFunctions CF;
	HttpSession session;
	String strId ;
	String strSalaryPaySlip;
	String orgId; 
	private String formatId;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		String operation = request.getParameter("operation");
		strId = request.getParameter("ID");
		
		UtilityFunctions uF = new UtilityFunctions();
		loadPaySlipSetting();
		
//		System.out.println("getFormatId============"+getFormatId());
		if(getFormatId()!= null && uF.parseToInt(getFormatId()) > 0 ) {
			setStrSalaryPaySlip(getFormatId());
		}
		if (operation!=null && operation.equals("E")) {
			return viewPaySlipSetting(strId);
		}
		
		if (getOrgId()!=null && getOrgId().length()>0) {
			return updatePaySlipSetting();
		}
		
		return LOAD;
	}
	
	public String viewPaySlipSetting(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setOrgId(rs.getString("org_id"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	public String updatePaySlipSetting() {

			Connection con = null;
			PreparedStatement pst = null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update org_details set payslip_format=? where org_id=?");
			pst.setInt(1, uF.parseToInt(getStrSalaryPaySlip()));
			pst.setInt(2, uF.parseToInt(getOrgId()));
			System.out.println("Pst===>"+pst);
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	public String loadPaySlipSetting() {
		paySlipFormatList = new FillPaySlipFormat().fillPaySlipFormat();
		return LOAD;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillPaySlipFormat> getPaySlipFormatList() {
		return paySlipFormatList;
	}

	public void setPaySlipFormatList(List<FillPaySlipFormat> paySlipFormatList) {
		this.paySlipFormatList = paySlipFormatList;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStrSalaryPaySlip() {
		return strSalaryPaySlip;
	}

	public void setStrSalaryPaySlip(String strSalaryPaySlip) {
		this.strSalaryPaySlip = strSalaryPaySlip;
	}

	public String getFormatId() {
		return formatId;
	}

	public void setFormatId(String formatId) {
		this.formatId = formatId;
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