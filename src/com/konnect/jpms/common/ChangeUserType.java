package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ChangeUserType extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	
	CommonFunctions CF = null;
	
	String empid;
	String userid; 
	
	String type;
	
	List<FillUserType> userTypeList;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	
	String userType;
	String strUserType;
	String[] orgId;
	String[] wLocation;
	String fromPage;
	
	List<String> orgValue = new ArrayList<String>();
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		if(CF==null)return LOGIN;
		strUserType  = (String)session.getAttribute(BASEUSERTYPE);
		
		userTypeList = new FillUserType(request).fillUserType(strUserType);
		wLocationList = new FillWLocation(request).fillWLocation();
		orgList = new FillOrganisation(request).fillOrganisation();
		
		if(getType()!=null && getType().equals("update")) {
			updateUsertype();
			if(getFromPage()!=null && getFromPage().equalsIgnoreCase("people")){
				return "people";
			} else {
				return SUCCESS;
			}
		} else if(getType()!=null && getType().equals("ajax")) {
//			checkUsername();
			return "ajax";
		} 
		viewUSerData();
		return LOAD;
	}
	
//	private void checkUsername() {
//
//		Connection con = null;
//		ResultSet rs = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("Select * from user_details where upper(username)=?");
//			pst.setString(1, getUsername().toUpperCase().trim());
//			rs=pst.executeQuery();
//			boolean flag=false;
//			while(rs.next()){
//				flag=true;
//			}
//			
//			int a=0;
//			if(flag){
//				a=1;
//			}
//			request.setAttribute("STATUS_MSG",""+a);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//
//	}

	private void updateUsertype() {

		Connection con = null;		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
//			System.out.println("hmUserType ===>> " + hmUserType);
//			System.out.println("getUserType() ===>> " + getUserType());
			
			if(hmUserType != null && hmUserType.get(getUserType()) != null && (hmUserType.get(getUserType()).equals(HRMANAGER) || hmUserType.get(getUserType()).equals(ACCOUNTANT) || hmUserType.get(getUserType()).equals(CEO) || hmUserType.get(getUserType()).equals(RECRUITER) || hmUserType.get(getUserType()).equals(OTHER_HR))) {
				StringBuilder sbOrgId = null;
				for(int i=0; getOrgId() != null && i<getOrgId().length; i++) {
					if(uF.parseToInt(getOrgId()[i]) > 0) {
						if(sbOrgId == null) {
							sbOrgId = new StringBuilder();
							sbOrgId.append(","+getOrgId()[i]+",");
						} else {
							sbOrgId.append(getOrgId()[i]+",");
						}
					}
				}
				
				StringBuilder sbwLocId = null;
				for(int i=0; wLocation != null && i<wLocation.length; i++) {
					if(uF.parseToInt(wLocation[i]) > 0) {
						if(sbwLocId == null) {
							sbwLocId = new StringBuilder();
							sbwLocId.append("," + wLocation[i]+",");
						} else {
							sbwLocId.append(wLocation[i]+",");
						}
					}
				}
				if(sbOrgId == null) sbOrgId = new StringBuilder();
				
				if(getOrgId() != null && getOrgId().length > 0 && (wLocation == null || wLocation.length==0)) {
					
//					String strOrgId = sbOrgId.substring(1, sbOrgId.length()-1).toString();
					String strOrgId = sbOrgId.length() > 0 ? sbOrgId.substring(1, sbOrgId.length()-1).toString() : "";
					if(strOrgId.length() > 0){
						pst = con.prepareStatement("select wlocation_id from work_location_info where org_id in("+ strOrgId +")");
						rs = pst.executeQuery();
						while (rs.next()) {
							if(rs.getInt("wlocation_id") > 0) {
								if(sbwLocId == null) {
									sbwLocId = new StringBuilder();
									sbwLocId.append("," + rs.getInt("wlocation_id") +",");
								} else {
									sbwLocId.append(rs.getInt("wlocation_id") +",");
								}
							}
						}
						rs.close();
						pst.close();
					}
				}
				
				if(sbwLocId == null) sbwLocId = new StringBuilder();
				
				pst = con.prepareStatement("update user_details set usertype_id=?, org_id_access=?, wlocation_id_access=? where user_id=? and emp_id=? ");
				pst.setInt(1, uF.parseToInt(getUserType()));
				pst.setString(2, sbOrgId.toString());
				pst.setString(3, sbwLocId.toString());
				pst.setInt(4, uF.parseToInt(getUserid()));
				pst.setInt(5, uF.parseToInt(getEmpid()));
				pst.execute();
				pst.close();
			} else {
//				Map<String, String> hmWlocation = CF.getEmpWlocationMap(con);
//				String ordId = CF.getEmpOrgId(con, uF, getEmpid());
//				pst = con.prepareStatement("update user_details set usertype_id=?, org_id_access=?, wlocation_id_access=? where user_id=? and emp_id=? ");
//				pst.setInt(1, uF.parseToInt(getUserType()));
//				pst.setString(2, ","+ordId+",");
//				pst.setString(3, ","+hmWlocation.get(getEmpid())+",");
//				pst.setInt(4, uF.parseToInt(getUserid()));
//				pst.setInt(5, uF.parseToInt(getEmpid()));
//				pst.execute();
				
				pst = con.prepareStatement("update user_details set usertype_id=?, org_id_access=?, wlocation_id_access=? where user_id=? and emp_id=? ");
				pst.setInt(1, uF.parseToInt(getUserType()));
				pst.setString(2, null);
				pst.setString(3, null);
				pst.setInt(4, uF.parseToInt(getUserid()));
				pst.setInt(5, uF.parseToInt(getEmpid()));
				pst.execute();
				pst.close();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void viewUSerData() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from user_details where user_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getUserid()));
			pst.setInt(2, uF.parseToInt(getEmpid()));
			rs = pst.executeQuery();
			while(rs.next()){
				setUserType(uF.showData(rs.getString("usertype_id"), ""));
				List<String> orgValue1 = new ArrayList<String>();
				if (rs.getString("org_id_access") == null) {
					orgValue1.add("0");
				} else {
					orgValue1 = Arrays.asList(rs.getString("org_id_access").split(","));
				}
				if (orgValue1 != null) {
					for (int i = 0; i < orgValue1.size(); i++) {
						if(uF.parseToInt(orgValue1.get(i)) > 0) {
							orgValue.add(orgValue1.get(i).trim());
						}
					}
				} else {
					orgValue.add("0");
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

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FillUserType> getUserTypeList() {
		return userTypeList;
	}

	public void setUserTypeList(List<FillUserType> userTypeList) {
		this.userTypeList = userTypeList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String[] getOrgId() {
		return orgId;
	}

	public void setOrgId(String[] orgId) {
		this.orgId = orgId;
	}

	public String[] getwLocation() {
		return wLocation;
	}

	public void setwLocation(String[] wLocation) {
		this.wLocation = wLocation;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public List<String> getOrgValue() {
		return orgValue;
	}

	public void setOrgValue(List<String> orgValue) {
		this.orgValue = orgValue;
	}

}
