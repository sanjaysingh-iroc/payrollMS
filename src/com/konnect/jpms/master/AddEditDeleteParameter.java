package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddEditDeleteParameter extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	String strOrg;
	String parameterId;
	String parameterName;
	String parameterDescription;
	List<FillOrganisation> orgList;
	String operation;
	
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	String strSessionEmpId;
	
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
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		loadValidateService();
		
		if (operation!=null && operation.equals("D")) {
			return deleteParameter();
		}
		if (operation!=null && operation.equals("E")) {
			return viewParameter();
		}
		
		if (getParameterId()!=null && getParameterId().length()>0) {
			return updateParameter();
		}
		
		if (getParameterName()!=null && getParameterName().length()>0) {
			return insertParameter();
		}
		System.out.println("in Parameterdiv add load");
		return LOAD;
		
	}

	public String loadValidateService() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			System.out.println("in addParameterdiv");
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
			String empOrgId = null;
			if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ADD_IN_LOGIN_USER_ORG)) && hmFeatureUserTypeId.get(IConstants.F_ADD_IN_LOGIN_USER_ORG).contains(strUsertypeId)) {
				empOrgId = (String)session.getAttribute(ORGID);
			}
			orgList = new FillOrganisation(request).fillOrganisation(empOrgId);
			
			if(getStrOrg()==null && orgList!=null && orgList.size()>0) {
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

	
	public String insertParameter() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into parameter_details (parameter_name, parameter_desc, org_id, added_by, entry_date) values (?,?,?,?, ?)");
			pst.setString(1, getParameterName());
			pst.setString(2, getParameterDescription());
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+getParameterName()+" saved successfully."+END);

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

	
	public String viewParameter() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from parameter_details where parameter_id=?");
			pst.setInt(1, uF.parseToInt(getParameterId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setParameterId(rs.getString("parameter_id"));
				setParameterName(rs.getString("parameter_name"));
				setParameterDescription(rs.getString("parameter_desc"));
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
	
	
	public String updateParameter() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
	
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE parameter_details SET parameter_name=?, parameter_desc=?, org_id=?, updated_by=?, update_date=? WHERE parameter_id=?");
			pst.setString(1, getParameterName());
			pst.setString(2, getParameterDescription());
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(6, uF.parseToInt(getParameterId()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getParameterName()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	

	public String deleteParameter() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from parameter_details where parameter_id=?");
			pst.setInt(1, uF.parseToInt(getParameterId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Parameter deleted successfully."+END);
			
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterDescription() {
		return parameterDescription;
	}

	public void setParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}

}