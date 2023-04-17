package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateStatutoryIdAndRegInfo extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	CommonFunctions CF;
	
	String orgId;
	String orgCode;
	String orgName;
	String orgPanNo;
	String orgTanNo;
	String orgAINCode;
	
	String[] orgStatutoryIds;
	
	String orgMCARegNo;
	String orgSTRegNo;
	
	String userscreen;
	String navigationId;
	String toPage;
	String fromPage;
	
	
	public String execute() throws Exception {

		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
    	System.out.println("strId==>"+strId);
    	System.out.println("getOrgId1==>"+getOrgId());
    	
		if (operation!=null && operation.equals("E")) {
			return viewOrganisation(strId);
		}
		
		System.out.println("getOrgId2==>"+getOrgId());
		if (getOrgId()!=null && getOrgId().length()>0) { 
			return updateOrganisation();
		}
		System.out.println("fromPage in java=="+fromPage);
		
		return LOAD;
	}
	
	public String updateOrganisation() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			
			String[] arr = {"org_reg_no", "org_pan_no", "org_tan_no", "org_ain_code", "org_esic_no", "trrn_epf", "epf_account_no", "establish_code_no", "tds_payment_code", "org_st_reg_no"};
			
			for(int i=0; i<getOrgStatutoryIds().length; i++){
				pst = con.prepareStatement("UPDATE org_details SET "+arr[i]+" = ? where org_id = ?");
				pst.setString(1, getOrgStatutoryIds()[i]);
				pst.setInt(2, uF.parseToInt(getOrgId()));			
				pst.execute();
				pst.close();
				
				boolean flag = false;
				pst = con.prepareStatement("select * from statutory_id_registration_info_history where statutory_type=? and statutory_value=? and org_id = ?" +
						" and stat_id_reg_info_id in (select max(stat_id_reg_info_id) as stat_id_reg_info_id from statutory_id_registration_info_history " +
						"where statutory_type=? and org_id = ?)");
				pst.setString(1, arr[i]);
				pst.setString(2, getOrgStatutoryIds()[i]);
				pst.setInt(3, uF.parseToInt(getOrgId()));
				pst.setString(4, arr[i]);
				pst.setInt(5, uF.parseToInt(getOrgId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
				
				if(!flag) {
					pst = con.prepareStatement("insert into statutory_id_registration_info_history (statutory_type,statutory_value, org_id, added_by, entry_date)" +
							" values(?,?,?,?,?)");
					pst.setString(1, arr[i]);
					pst.setString(2, getOrgStatutoryIds()[i]);
					pst.setInt(3, uF.parseToInt(getOrgId()));
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.execute();
					pst.close();
				}
				
			}
			session.setAttribute(MESSAGE, SUCCESSM+getOrgName()+"'s Statutory Id's & Registration Information updated successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewOrganisation(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from org_details where org_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			Map<String, String> hmStatutoryIds = new HashMap<String, String>(); 
			while(rs.next()){
				
				setOrgId(rs.getString("org_id"));
				setOrgCode(rs.getString("org_code"));
				setOrgName(rs.getString("org_name"));
				
				hmStatutoryIds.put("ORG_PAN_NO",rs.getString("org_pan_no"));
				hmStatutoryIds.put("ORG_TAN_NO",rs.getString("org_tan_no"));
				hmStatutoryIds.put("ORG_AIN_CODE",rs.getString("org_ain_code"));
				hmStatutoryIds.put("ORG_ESIC_NO",rs.getString("org_esic_no"));
				hmStatutoryIds.put("ORG_TRRN_EPF",rs.getString("trrn_epf"));
				hmStatutoryIds.put("ORG_EPF_ACC_NO",rs.getString("epf_account_no"));
				hmStatutoryIds.put("ORG_ESTABLISH_CODE_NO",rs.getString("establish_code_no"));
				hmStatutoryIds.put("ORG_TDS_PAYMENT_CODE",rs.getString("tds_payment_code"));
				
				hmStatutoryIds.put("ORG_REG_NO",rs.getString("org_reg_no"));
				hmStatutoryIds.put("ORG_ST_REG_CODE",rs.getString("org_st_reg_no"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmStatutoryIds",hmStatutoryIds);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgPanNo() {
		return orgPanNo;
	}

	public void setOrgPanNo(String orgPanNo) {
		this.orgPanNo = orgPanNo;
	}

	public String getOrgTanNo() {
		return orgTanNo;
	}

	public void setOrgTanNo(String orgTanNo) {
		this.orgTanNo = orgTanNo;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getOrgAINCode() {
		return orgAINCode;
	}


	public void setOrgAINCode(String orgAINCode) {
		this.orgAINCode = orgAINCode;
	}

	public String[] getOrgStatutoryIds() {
		return orgStatutoryIds;
	}

	public void setOrgStatutoryIds(String[] orgStatutoryIds) {
		this.orgStatutoryIds = orgStatutoryIds;
	}

	public String getOrgMCARegNo() {
		return orgMCARegNo;
	}

	public void setOrgMCARegNo(String orgMCARegNo) {
		this.orgMCARegNo = orgMCARegNo;
	}

	public String getOrgSTRegNo() {
		return orgSTRegNo;
	}

	public void setOrgSTRegNo(String orgSTRegNo) {
		this.orgSTRegNo = orgSTRegNo;
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
	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
}