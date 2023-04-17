package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClientPoc;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTaskService extends ActionSupport implements ServletRequestAware, IStatements {
	HttpServletRequest request;
	
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	
	String[] service;
	List<String> serviceID = new ArrayList<String>();
	String sdesc; 
//	List<FillLevel> levelList;
//	List<FillWLocation> wLocationList;
	
	List<FillServices> serviceList;
	String operation;
	String ID;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		request.setAttribute(PAGE, "/jsp/task/AddTaskService.jsp");
		request.setAttribute(TITLE, "Add New Project Service");
		
		

		loadValidateService();
		
		if(operation!=null){
			if(operation.equals("E")) {
				getProjectService();
			} else if(operation.equals("D")) {
				deleteProjectService();
				return "update";
			} else if(operation.equals("A")) {
				updateProjectService();
				return "update";
			}
		} else if(service!=null) {
			setProjectService();
			return "update";
		}
		
		return SUCCESS;
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
			if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_LOGIN_USER_ORG_SBU_IN_ADD_SERVICE)) && hmFeatureUserTypeId.get(IConstants.F_SHOW_LOGIN_USER_ORG_SBU_IN_ADD_SERVICE).contains(strUsertypeId)) {
				empOrgId = (String)session.getAttribute(ORGID);
			}
			serviceList = new FillServices(request).fillServices(empOrgId, uF);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	public void getProjectService() {
			UtilityFunctions uF=new UtilityFunctions();
			Database db = new Database();
			db.setRequest(request);
			Connection con = null;
			PreparedStatement pst=null;
			ResultSet rs=null;
			try {

				con = db.makeConnection(con);
				pst = con.prepareStatement("select * from services_project where service_project_id=? ");
				pst.setInt(1, uF.parseToInt(ID));
				rs=pst.executeQuery();
				while(rs.next()){
					setServiceName(rs.getString("service_name"));
					setSdesc(rs.getString("service_desc"));
//						setService(rs.getString("service_id"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select sbu_id from services_project_sbu where service_pro_id=? ");
				pst.setInt(1, uF.parseToInt(ID));
				rs=pst.executeQuery();
				while(rs.next()) {
					serviceID.add(rs.getString("sbu_id"));
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
	
	
	public void updateProjectService() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("update services_project set service_name=?,service_desc=? where service_project_id=?");
			pst.setString(1, getServiceName());
			pst.setString(2, getSdesc());
//			pst.setInt(3, uF.parseToInt(getService()));
			pst.setInt(3, uF.parseToInt(getID()));
			pst.execute();
			pst.close();
			
			if(getService() != null && (getService().length > 1 || (getService().length == 1 && uF.parseToInt(getService()[0]) > 0))) {
				StringBuilder sbServices = null;
				for(int i=0; i<getService().length; i++) {
					if(uF.parseToInt(getService()[i]) > 0) {
						boolean flag = false;
						pst = con.prepareStatement("select sbu_id from services_project_sbu where service_pro_id = ? and sbu_id=?");
						pst.setInt(1, uF.parseToInt(getID()));
						pst.setInt(2, uF.parseToInt(getService()[i]));
						rs = pst.executeQuery();
						while(rs.next()) {
							flag = true;
						}
						rs.close();
						pst.close();
						if(!flag) {
							pst = con.prepareStatement("insert into services_project_sbu(service_pro_id,sbu_id) values(?,?) ");
							pst.setInt(1, uF.parseToInt(getID()));
							pst.setInt(2, uF.parseToInt(getService()[i]));
							pst.execute();
							pst.close();
						}
						if(sbServices == null) {
							sbServices = new StringBuilder();
							sbServices.append(getService()[i]);
						} else {
							sbServices.append(","+getService()[i]);
						}
					}
				}
				if(sbServices != null) {
					pst = con.prepareStatement("delete from services_project_sbu where service_pro_id=? and sbu_id not in("+sbServices.toString()+")");
					pst.setInt(1, uF.parseToInt(getID()));
					pst.execute();
					pst.close();
				}
				
			} else {
				for(int i=0; serviceList != null && !serviceList.isEmpty() && i<serviceList.size(); i++) {
					boolean flag = false;
					pst = con.prepareStatement("select sbu_id from services_project_sbu where service_pro_id = ? and sbu_id=?");
					pst.setInt(1, uF.parseToInt(getID()));
					pst.setInt(2, uF.parseToInt(serviceList.get(i).getServiceId()));
					rs = pst.executeQuery();
					while(rs.next()) {
						flag = true;
					}
					rs.close();
					pst.close();
					if(!flag) {
						pst = con.prepareStatement("insert into services_project_sbu(service_pro_id,sbu_id) values(?,?) ");
						pst.setInt(1, uF.parseToInt(getID()));
						pst.setInt(2, uF.parseToInt(serviceList.get(i).getServiceId()));
						pst.execute();
						pst.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	
	public void deleteProjectService() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from services_project where service_project_id=? ");
			pst.setInt(1, uF.parseToInt(getID()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from services_project_sbu where service_pro_id=? ");
			pst.setInt(1, uF.parseToInt(getID()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void setProjectService(){

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into services_project(service_name,service_desc) values(?,?) ");
			pst.setString(1, serviceName);
			pst.setString(2, sdesc);
//			pst.setInt(3, uF.parseToInt(service));
			pst.execute();
			pst.close();
			
			int service_project_id = 0;
			pst = con.prepareStatement("select max(service_project_id) as service_project_id from services_project");
			rs = pst.executeQuery();
			while(rs.next()) {
				service_project_id = rs.getInt("service_project_id");
			}
			rs.close();
			pst.close();
			
//			System.out.println("getService() ====>> " + getService().length);
			if(getService() != null && (getService().length > 1 || (getService().length == 1 && uF.parseToInt(getService()[0]) > 0))) {
				
				for(int i=0; i<getService().length; i++) {
					if(uF.parseToInt(getService()[i]) > 0) {
						pst = con.prepareStatement("insert into services_project_sbu(service_pro_id,sbu_id) values(?,?) ");
						pst.setInt(1, service_project_id);
						pst.setInt(2, uF.parseToInt(getService()[i]));
						pst.execute();
						pst.close();
					}
				}
			} else {
				for(int i=0; serviceList != null && !serviceList.isEmpty() && i<serviceList.size(); i++) {
					pst = con.prepareStatement("insert into services_project_sbu(service_pro_id,sbu_id) values(?,?) ");
					pst.setInt(1, service_project_id);
					pst.setInt(2, uF.parseToInt(serviceList.get(i).getServiceId()));
					pst.execute();
					pst.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	String serviceName;
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String[] getService() {
		return service;
	}

	public void setService(String[] service) {
		this.service = service;
	}

	public String getSdesc() {
		return sdesc;
	}

	public void setSdesc(String sdesc) {
		this.sdesc = sdesc;
	}

	public List<String> getServiceID() {
		return serviceID;
	}

	public void setServiceID(List<String> serviceID) {
		this.serviceID = serviceID;
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
