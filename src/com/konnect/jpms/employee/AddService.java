package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddService extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	String strOrg;
	String serviceId;
	String serviceName;
	String serviceDescription;
	String serviceCode;
	List<FillOrganisation> orgList;
	
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {

//		request.setAttribute(PAGE, PAddService);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		loadValidateService();
		
		if (operation!=null && operation.equals("D")) {
			return deleteService(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewServices(strId);
		}
		
		if (getServiceId()!=null && getServiceId().length()>0) {
			return updateService();
		}
		if (getServiceCode()!=null && getServiceCode().length()>0) {
			return insertService();
		}
		
		return LOAD;
		
	}

	public String loadValidateService() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
			String empOrgId = null;
			if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ADD_IN_LOGIN_USER_ORG)) && hmFeatureUserTypeId.get(IConstants.F_ADD_IN_LOGIN_USER_ORG).contains(strUsertypeId)) {
				empOrgId = (String)session.getAttribute(ORGID);
			}
			orgList = new FillOrganisation(request).fillOrganisation(empOrgId);
			if(getStrOrg()==null && orgList!=null && orgList.size()>0){
				setStrOrg((String)session.getAttribute(ORGID));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	public String insertService() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertService);
			pst.setString(1, getServiceName());
			pst.setString(2, getServiceCode());
			pst.setString(3, getServiceDescription());
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getServiceName()+" saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	public String viewServices(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectServiceV);
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setServiceId(rs.getString("service_id"));
				setServiceCode(rs.getString("service_code"));
				setServiceName(rs.getString("service_name"));
				setServiceDescription(rs.getString("service_desc"));
				setStrOrg(rs.getString("org_id"));
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
	
	
	public String updateService() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
	
		String updateService = "UPDATE services SET service_name=?, service_code=?, service_desc=?, org_id=? WHERE service_id=?";
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateService);
			pst.setString(1, getServiceName());
			pst.setString(2, getServiceCode());
			pst.setString(3, getServiceDescription());
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.setInt(5, uF.parseToInt(getServiceId()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getServiceName()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	

	public String deleteService(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteService);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
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

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
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