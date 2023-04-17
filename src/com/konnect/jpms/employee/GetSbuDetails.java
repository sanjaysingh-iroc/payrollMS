package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSbuDetails extends ActionSupport implements ServletRequestAware{

	String parentDept;
	String service_id;
	String orgId;
	String strSerivce;
	String type;
	
	private static final long serialVersionUID = 1L;

	List<FillServices> serviceList;
	
	List<FillDepartment> departList;
	
	List<FillLevel> levelList;
	public String execute() {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getOrgId() != null && !getOrgId().equals("") && type != null && type.equals("SBU")) {
			serviceList = new FillServices().fillServicesBYORG(getOrgId(), uF);
		}
		if(getOrgId() != null && !getOrgId().equals("") && type != null && type.equals("LEVEL")) {
			levelList = new FillLevel(request).fillLevelBYORG(uF.parseToInt(getOrgId()));
		}
		//getDepartmentListByService();
		if(getOrgId() != null && !getOrgId().equals("") && getStrSerivce() != null && !getStrSerivce().equals("") && type != null && type.equals("DEPT")) {
			departList=new FillDepartment(request).fillDepartmentBYSBU(getStrSerivce(),getOrgId());
		}
		return SUCCESS;
		
	}


	private void getDepartmentListByService() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String serviceid=null;
		UtilityFunctions uF=new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select dept_id,dept_name from department_info where service_id=?");
			pst.setInt(1, uF.parseToInt(getStrSerivce()));
			departList=new ArrayList<FillDepartment>();
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
//				departList.add(new FillDepartment(rs.getString("dept_id"),rs.getString("dept_name")));
				departList.add(new FillDepartment(rs.getString("dept_id"), rs.getString("dept_name")));
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
		
	}


	private String getServiceIDByParentDept(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String serviceid=null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select service_id from department_info where dept_id=?");
			pst.setInt(1, uF.parseToInt(getParentDept()));
			
			rs = pst.executeQuery();
			while (rs.next()) {
				serviceid=rs.getString("service_id");
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
		return serviceid;
	}


	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	public String getParentDept() {
		return parentDept;
	}

	public void setParentDept(String parentDept) {
		this.parentDept = parentDept;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}


	public String getStrSerivce() {
		return strSerivce;
	}


	public void setStrSerivce(String strSerivce) {
		this.strSerivce = strSerivce;
	}


	public List<FillDepartment> getDepartList() {
		return departList;
	}


	public void setDepartList(List<FillDepartment> departList) {
		this.departList = departList;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}
	
}
