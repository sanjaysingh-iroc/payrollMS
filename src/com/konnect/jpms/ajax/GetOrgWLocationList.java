package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrgWLocationList extends ActionSupport implements ServletRequestAware{

	
	private List<FillWLocation> wLocationList;
	private List<FillWLocation> wLocationList1;
	
	private String type;
	private String strOrgId;
	private String empid;
	
	private List<String> wLocValue = new ArrayList<String>();
	
	private static final long serialVersionUID = 1L;

	public String execute() {
	
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String strOId = request.getParameter("OID");
//		System.out.println("strOrgId ===>> " + strOrgId);
//		System.out.println("type ===>> " + type);
		if(getType() != null && getType().equals("UTChange")) {

			if(strOrgId == null || strOrgId.equals("")) {
				strOrgId = "0";
			}
			wLocationList = new FillWLocation(request).fillWLocationOrgIdAndWLocationIds(strOrgId, null);
			getEmpSelectedLocationId(uF);
		} else if(getType() != null && getType().equalsIgnoreCase("CXOLOCATION")) {
			if(strOrgId == null || strOrgId.equals("")) {
				strOrgId = "0";
			}
			wLocationList = new FillWLocation(request).fillWLocationOrgIdAndWLocationIds(strOrgId, null);
		} else if(getType() != null && getType().equalsIgnoreCase("people")) {
			if(strOrgId == null || strOrgId.equals("")) {
				strOrgId = "0";
			}
			wLocationList1 = new FillWLocation(request).fillWLocationOrgIdAndWLocationIds(strOrgId, null);
			getEmpSelectedLocationId(uF);
		} else if(getType() != null && getType().equalsIgnoreCase("VC")) {
			
			if(strOrgId == null || strOrgId.equals("")) {
				strOrgId = "0";
			}
			wLocationList = new FillWLocation(request).fillWLocationOrgIdAndWLocationIds(strOrgId, null);
		} else {
			wLocationList = new FillWLocation(request).fillWLocation(strOId);
		}
		
		return SUCCESS;
	}

	
	private void getEmpSelectedLocationId(UtilityFunctions uF) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select wlocation_id_access from user_details where emp_id=? ");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> wLocValue1 = new ArrayList<String>();
				if (rs.getString("wlocation_id_access") == null) {
					wLocValue1.add("0");
				} else {
					wLocValue1 = Arrays.asList(rs.getString("wlocation_id_access").split(","));
				}
				if (wLocValue1 != null) {
					for (int i = 0; i < wLocValue1.size(); i++) {
						if(uF.parseToInt(wLocValue1.get(i)) > 0) {
							wLocValue.add(wLocValue1.get(i).trim());
						}
					}
				} else {
					wLocValue.add("0");
				}
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
	}
	

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	
	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getStrOrgId() {
		return strOrgId;
	}
	
	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}
	
	public List<FillWLocation> getwLocationList1() {
		return wLocationList1;
	}
	
	public void setwLocationList1(List<FillWLocation> wLocationList1) {
		this.wLocationList1 = wLocationList1;
	}
	
	public String getEmpid() {
		return empid;
	}
	
	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public List<String> getwLocValue() {
		return wLocValue;
	}

	public void setwLocValue(List<String> wLocValue) {
		this.wLocValue = wLocValue;
	}
	
}
