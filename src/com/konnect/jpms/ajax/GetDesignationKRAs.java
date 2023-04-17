package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetDesignationKRAs extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	
	String strSessionEmpId = null;
	CommonFunctions CF;
	
	public String execute() throws Exception {

		request.setAttribute(PAGE, PAddDesignation);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
//		System.out.println("getDesigId() ===>>> " + getDesigId());
//		System.out.println("getOrgId() ===>>> " + getOrgId());
		getDesignKRAs();
		
		return SUCCESS;
		
	}

	private void getDesignKRAs() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from goal_kras where emp_ids like '%,"+getEmpId()+",%' and goal_type = "+EMPLOYEE_KRA+" " +
					" and desig_kra_id != 0 order by desig_kra_id");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			List<List<String>> desigKraDetails = new ArrayList<List<String>>();
			StringBuilder desigKraIds = new StringBuilder();
			while(rs.next()) {
				desigKraIds.append(rs.getString("desig_kra_id")+",");
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("desig_kra_id"));
				innerList.add(rs.getString("kra_description"));
				innerList.add(CF.getElementDetails(con, request, rs.getString("element_id")));
				innerList.add(rs.getString("element_id"));
				innerList.add(CF.getAttributeListElementwise(con, uF, request, getOrgId(), rs.getString("element_id"), rs.getString("attribute_id")));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("is_assign").equals("t") ? "1" : "0");
				innerList.add(rs.getString("is_assign").equals("t") ? "checked" : "");
				innerList.add(rs.getString("goal_kra_id"));
				Map<String, String> hmKRATaskData = getKRATaskDetails(con, uF, rs.getString("goal_kra_id"));
				innerList.add(hmKRATaskData.get("ID"));
				innerList.add(hmKRATaskData.get("NAME"));
				desigKraDetails.add(innerList);
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from designation_kra_details where designation_id=? ");
			if(!desigKraIds.toString().equals("")) {
				String dkIds = desigKraIds.toString().substring(0, desigKraIds.toString().length()-1);
				sbQuery.append("and designation_kra_id not in ("+dkIds+") ");
			}
			sbQuery.append(" order by designation_kra_id");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			pst.setInt(1, uF.parseToInt(getDesigId()));
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("designation_kra_id"));
				innerList.add(rs.getString("kra_name"));
				innerList.add(CF.getElementDetails(con, request, rs.getString("element_id")));
				innerList.add(rs.getString("element_id"));
				innerList.add(CF.getAttributeListElementwise(con, uF, request, getOrgId(), rs.getString("element_id"), rs.getString("attribute_id")));
				innerList.add(rs.getString("attribute_id"));
				innerList.add("0");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("task_name"));
				desigKraDetails.add(innerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("desigKraDetails ===>> " + desigKraDetails);
			request.setAttribute("desigKraDetails", desigKraDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private Map<String, String> getKRATaskDetails(Connection con, UtilityFunctions uF, String kraId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmKraTaskData = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from goal_kra_tasks where kra_id = ?");
			pst.setInt(1, uF.parseToInt(kraId));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				hmKraTaskData.put("ID", rs.getString("goal_kra_task_id"));
				hmKraTaskData.put("NAME", rs.getString("task_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmKraTaskData;
	}

	private String empId;
	private String desigId;
	private String orgId;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getDesigId() {
		return desigId;
	}

	public void setDesigId(String desigId) {
		this.desigId = desigId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

}