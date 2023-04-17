package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillServices implements IStatements{

	String serviceId;
	String serviceName;
	
	
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
	
	public FillServices(String serviceId, String serviceName) {
		this.serviceId = serviceId;
		this.serviceName = serviceName;
	}
	
	HttpServletRequest request;
	public FillServices(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillServices() {
	}
	
	public List<FillServices> fillServices() {
		List<FillServices> al = new ArrayList<FillServices>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectService);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillServices(rs1.getString("service_id"), rs1.getString("service_name")));
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillServices> fillServicesByOrgIds(String strOrgIds) {
		List<FillServices> al = new ArrayList<FillServices>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			if(strOrgIds != null && !strOrgIds.trim().equals("")) {
				pst = con.prepareStatement("SELECT s.*,od.org_name,od.org_code FROM services s, org_details od where s.org_id = od.org_id and s.org_id in ("+strOrgIds+") order by service_name");
			} else {
				pst = con.prepareStatement(selectService);
			}
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillServices(rs1.getString("service_id"), rs1.getString("service_name")));
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillServices> fillServices(String strOrgId, UtilityFunctions uF){
		List<FillServices> al = new ArrayList<FillServices>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			if(uF.parseToInt(strOrgId)>0) {
				pst = con.prepareStatement(selectServiceR1);
				pst.setInt(1, uF.parseToInt(strOrgId));
			} else {
				pst = con.prepareStatement(selectService);
			}
			
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillServices(rs1.getString("service_id"), rs1.getString("service_name")));
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillServices> fillProjectServices() {
		List<FillServices> al = new ArrayList<FillServices>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from services_project order by service_name");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillServices(rs1.getString("service_project_id"), rs1.getString("service_name")));
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillServices> fillServices(String strEmpId) {
		List<FillServices> al = new ArrayList<FillServices>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String []arrServices = null;
			pst = con.prepareStatement("select * from employee_official_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				arrServices = rs1.getString("service_id").split(",");
			}	
			rs1.close();
			pst.close();
			
			pst = con.prepareStatement(selectService);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(ArrayUtils.contains(arrServices, rs1.getString("service_id"))>=0) {
					al.add(new FillServices(rs1.getString("service_id"), rs1.getString("service_name")));
				}
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public String fillServicesHtml() {
		StringBuilder sb = new StringBuilder();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectService);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				sb.append("<option value=\""+rs1.getString("service_id")+"\">"+rs1.getString("service_name")+"</option>");
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sb.toString();
	}

	
	public List<FillServices> fillServicesBYORG(String strOrgId, UtilityFunctions uF) {
		List<FillServices> al = new ArrayList<FillServices>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectServiceR1);
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillServices(rs1.getString("service_id"), rs1.getString("service_name")));
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	
	public String getSBUIdOnProjectServiceId(UtilityFunctions uF, String serviceId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		String sbuId = "";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from services_project where service_project_id = ?");
			pst.setInt(1, uF.parseToInt(serviceId));
//			System.out.println("pst =======>> " + pst);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				sbuId = rs1.getString("service_id");
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbuId;
	}

	public List<FillServices> fillServicesWithoutCurrentService(int nEmpId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		String sbuId = "";
		List<FillServices> al = new ArrayList<FillServices>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select service_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?");
			pst.setInt(1, nEmpId);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
			//===start parvez date: 15-02-2023===	
//				sbuId = rs1.getString("service_id");
				if(rs1.getString("service_id") != null){
					sbuId = rs1.getString("service_id");
				}
			//===end parvez date: 15-02-2023===	
			}	
			rs1.close();
			pst.close();
			
			List<String> alList = Arrays.asList(sbuId.split(","));
			StringBuilder sbServices = null;
			for(int i=0;i<alList.size();i++){
		      if(alList.get(i)!=null && !alList.get(i).trim().equals("")){
		    	  if(sbServices==null){
		    		  sbServices = new StringBuilder();
		    		  sbServices.append(alList.get(i));
		    	  } else {
		    		  sbServices.append(","+alList.get(i));
		    	  }
		      }
			}
			
			if(sbServices!=null){
				pst = con.prepareStatement("select * from services where org_id in (select org_id from employee_personal_details epd, " +
						"employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?) and service_id not in ("+sbServices.toString()+") order by service_name");
				pst.setInt(1, nEmpId);
				rs1 = pst.executeQuery();
				while (rs1.next()) {
					al.add(new FillServices(rs1.getString("service_id"), rs1.getString("service_name")));
				}	
				rs1.close();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
}

