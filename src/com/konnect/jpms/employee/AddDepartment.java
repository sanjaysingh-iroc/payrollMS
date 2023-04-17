package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author user
 *
 */
public class AddDepartment extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
//	List<FillWLocation> wLocationList;
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	
	String deptId; 
	String deptName;
	String deptDescription; 
	String deptContactNo; 
	String deptFax;
	String deptCode; 
	String strOrg;
	
	List<FillOrganisation> orgList;
//	List<FillServices> serviceList;
//	List<FillDepartment> departList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		 
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
//		request.setAttribute(PAGE, PAddDepartment);
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
//		UtilityFunctions uF=new UtilityFunctions();
//		wLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
//		serviceList = new FillServices(request).fillServicesBYORG("0", new UtilityFunctions());
//		departList = new FillDepartment(request).fillDepartmentBYSBU("0", "0");
		
		System.out.println("getDeptId() ===>> " + getDeptId());
		
		loadValidateDepartment();
		if (operation!=null && operation.equals("D")) {
			return deleteDepartment(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewDepartment(strId);
		}
		
		if (getDeptId()!=null && getDeptId().length()>0) {
				return updateDepartment();
		}
		if (getDeptName()!=null && getDeptName().length()>0) {
				return insertDepartment();
		}
//		System.out.println("getDeptId() ===>> before load ===>> " + getDeptId());
		return LOAD;
	}
	
	
	public String loadValidateDepartment() {
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
	
	
	
	public String viewDepartment(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectDepartmentV);
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setDeptId(rs.getString("dept_id"));
				setDeptName(rs.getString("dept_name"));
				setDeptCode(rs.getString("dept_code"));
				setDeptContactNo(rs.getString("dept_contactno"));
				setDeptDescription(rs.getString("dept_desc"));
				setDeptFax(rs.getString("dept_faxno"));
				setStrOrg(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
//			serviceList = new FillServices(request).fillServices(getStrOrg(), new UtilityFunctions());
//			departList = new FillDepartment(request).fillDepartmentBYSBU(getService_id(), getStrOrg());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}

	
	public String insertDepartment() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs=null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("INSERT INTO department_info (dept_name, dept_code, dept_desc, parent, dept_contactno, " +
					"dept_faxno, org_id) VALUES (?,?,?,?, ?,?,?)");
			pst.setString(1, getDeptName());
			pst.setString(2, getDeptCode());
			pst.setString(3, getDeptDescription());
			pst.setInt(4, 0);
			pst.setString(5, getDeptContactNo());
			pst.setString(6, getDeptFax());
			pst.setInt(7, uF.parseToInt(getStrOrg()));
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM+getDeptName()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
			return ERROR;
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String updateDepartment() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs=null;
		
		try {
			con = db.makeConnection(con);
			String updateDepartment = "UPDATE department_info SET dept_name=?,dept_code=?, dept_desc=?, " +
					"parent=?,dept_contactno=?, dept_faxno=?, org_id=? WHERE dept_id=?";
			pst = con.prepareStatement(updateDepartment);
			
			pst.setString(1, getDeptName());
			pst.setString(2, getDeptCode());
			pst.setString(3, getDeptDescription());
			pst.setInt(4, 0);
			pst.setString(5, getDeptContactNo());
			pst.setString(6, getDeptFax());
			pst.setInt(7, uF.parseToInt(getStrOrg()));
			pst.setInt(8, uF.parseToInt(getDeptId()));
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM+getDeptName()+" updated successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String deleteDepartment(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteDepartment);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteDepartment1);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	public void validate() {
		
        if (getDeptName()!=null && getDeptName().length() == 0) {
            addFieldError("deptName", "Department Name is required");
        } 
    }
	
	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptDescription() {
		return deptDescription;
	}

	public void setDeptDescription(String deptDescription) {
		this.deptDescription = deptDescription;
	}

	public String getDeptContactNo() {
		return deptContactNo;
	}

	public void setDeptContactNo(String deptContactNo) {
		this.deptContactNo = deptContactNo;
	}

	public String getDeptFax() {
		return deptFax;
	}

	public void setDeptFax(String deptFax) {
		this.deptFax = deptFax;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
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