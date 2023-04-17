package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetProjectOwnerOrgDetails extends ActionSupport implements ServletRequestAware{

	 String proOwnerId;
	 String organisation;
	 List<FillDepartment> departmentList; 
	 List<FillServices> sbuList;
	 List<FillWLocation> workLocationList;
	 List<FillOrganisation> organisationList;
	 
	 private static final long serialVersionUID = 1L;

	 public String execute() {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		getProOwnerOrgId(uF);
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		
//		System.out.println("GetDepartmentByOrganization ...........");
		return SUCCESS;			
	 }


	private void getProOwnerOrgId(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select org_id from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(getProOwnerId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setOrganisation(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrganisation()));
			sbuList = new FillServices(request).fillServices(getOrganisation(), uF);
			workLocationList = new FillWLocation(request).fillWLocation(getOrganisation());
//			System.out.println("sbEmpIds ===>>> " + sbEmpIds);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public String getProOwnerId() {
		return proOwnerId;
	}

	public void setProOwnerId(String proOwnerId) {
		this.proOwnerId = proOwnerId;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public List<FillServices> getSbuList() {
		return sbuList;
	}

	public void setSbuList(List<FillServices> sbuList) {
		this.sbuList = sbuList;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

	
}
