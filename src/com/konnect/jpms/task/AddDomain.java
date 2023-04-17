package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddDomain extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	
	String operation;
	String ID;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	String domainCode;
	String domainName;
	String domainDesc;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		request.setAttribute(PAGE, "/jsp/task/AddDomain.jsp");
		request.setAttribute(TITLE, "Add New Project Domain");
		
		if(operation!=null){
			if(operation.equals("E")) {
				getProjectDomain();
			} else if(operation.equals("D")) {
				deleteProjectDomain();
				return "update";
			} else if(operation.equals("A")) {
				updateProjectDomain();
				return "update";
			}
		} else if(getDomainName()!=null) {
			insertProjectDomain();
			return "update";
		}
		
		return SUCCESS;
	}
	
	public void getProjectDomain() {
		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from domain_details where domain_id=? ");
			pst.setInt(1, uF.parseToInt(ID));
			rs=pst.executeQuery();
			while(rs.next()){
				setDomainCode(rs.getString("domain_code"));
				setDomainName(rs.getString("domain_name"));
				setDomainDesc(rs.getString("domain_description"));
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
	
	public void deleteProjectDomain() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from domain_details where domain_id=? ");
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
	
	public void updateProjectDomain() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("update domain_details set domain_code=?,domain_name=?, domain_description=? where domain_id=?");
			pst.setString(1, getDomainCode());
			pst.setString(2, getDomainName());
			pst.setString(3, getDomainDesc());
			pst.setInt(4, uF.parseToInt(getID()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	
	public void insertProjectDomain(){

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into domain_details(domain_code,domain_name,domain_description) values(?,?,?) ");
			pst.setString(1, getDomainCode());
			pst.setString(2, getDomainName());
			pst.setString(3, getDomainDesc());
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
	
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

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainDesc() {
		return domainDesc;
	}

	public void setDomainDesc(String domainDesc) {
		this.domainDesc = domainDesc;
	}
	
}
