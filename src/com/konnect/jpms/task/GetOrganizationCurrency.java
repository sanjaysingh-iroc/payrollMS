package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrganizationCurrency extends ActionSupport implements ServletRequestAware, IStatements {
	HttpServletRequest request;
	
	CommonFunctions CF; 
	HttpSession session;
	String strSessionEmpId;
	
	String locationId;
	String orgId;
	
	String from;
	String type;
	
	String strCurrency;
	String strBillingCurrency;
	
	List<FillCurrency> currencyList;

	public String execute() throws Exception {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		if(getFrom() != null && getFrom().equals("AddPRate")) {
			getOrgCurrencyType();
		} else if(getFrom() != null && getFrom().equals("AddPro")) {
			getOrgCurrId();
		}
		
		return SUCCESS;
	}
	
	private void getOrgCurrId() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		try {

			con = db.makeConnection(con);
			currencyList= new FillCurrency(request).fillCurrency();
			String currId = CF.getOrgCurrencyIdByOrg(con, orgId);
			setStrCurrency(currId);
			setStrBillingCurrency(currId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
	}

	
	public void getOrgCurrencyType() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {

			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCurrData = CF.getCurrencyDetails(con);
			String currId = "";
			pst = con.prepareStatement("select org_currency from org_details where org_id = (select org_id from work_location_info where wlocation_id = ?)");
			pst.setInt(1, uF.parseToInt(locationId));
			rs=pst.executeQuery();
			while(rs.next()) {
				currId = rs.getString("org_currency");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmCurr = hmCurrData.get(currId);
			String currType = "";
			if(hmCurr != null) {
				currType = hmCurr.get("SHORT_CURR");
			}
			
//			System.out.println("SHORT_CURR ===>>> " + hmCurr.get("SHORT_CURR"));
			request.setAttribute("OrgCurrency", currType);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getStrBillingCurrency() {
		return strBillingCurrency;
	}

	public void setStrBillingCurrency(String strBillingCurrency) {
		this.strBillingCurrency = strBillingCurrency;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		// TODO Auto-generated method stub
		
	}
	
	
}
