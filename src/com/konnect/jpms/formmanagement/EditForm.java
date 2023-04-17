package com.konnect.jpms.formmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillNodes;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditForm extends ActionSupport implements ServletRequestAware, IStatements{

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strOrg;
	String strFormName;
	String strNode;
	
	List<FillOrganisation> orgList;
	List<FillNodes> nodeList;
	
	String formId;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute(){
		session= request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		 
		String operation = (String) request.getParameter("operation");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getFormId()) > 0 && operation !=null && operation.trim().equalsIgnoreCase("U")){
			editForm(uF);
			return SUCCESS;
		} 
		getFormDetails(uF);
		
		return loadAddFormList(uF);
	} 
	
	private void editForm(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update form_management_details set form_name=?,node_id=?,org_id=? where form_id=?");
			pst.setString(1, getStrFormName());
			pst.setInt(2, uF.parseToInt(getStrNode()));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(getFormId()));
			pst.executeUpdate();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getFormDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmNodes = CF.getNodes(con);
			pst = con.prepareStatement("select * from form_management_details where form_id=?");
			pst.setInt(1, uF.parseToInt(getFormId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrFormName(uF.showData(rs.getString("form_name"), ""));
				setStrOrg(rs.getString("org_id"));
				setStrNode(rs.getString("node_id"));
				
				request.setAttribute("strNodeName",uF.showData(hmNodes.get(rs.getString("node_id")), ""));
			}
			rs.close();
			pst.close();
			
			String strOrgName = CF.getOrgNameById(con, getStrOrg());
			request.setAttribute("strOrgName",strOrgName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String loadAddFormList(UtilityFunctions uF) {
		orgList = new FillOrganisation(request).fillOrganisation();
		nodeList = new FillNodes(request).fillFormNodes("F", CF, uF.parseToInt(getStrOrg()), uF.parseToInt(getFormId()));
		
		return LOAD;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrFormName() {
		return strFormName;
	}

	public void setStrFormName(String strFormName) {
		this.strFormName = strFormName;
	}

	public String getStrNode() {
		return strNode;
	}

	public void setStrNode(String strNode) {
		this.strNode = strNode;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillNodes> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<FillNodes> nodeList) {
		this.nodeList = nodeList;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
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