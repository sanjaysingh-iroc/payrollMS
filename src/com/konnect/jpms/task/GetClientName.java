package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author workrig
 *
 */
public class GetClientName extends ActionSupport implements
		ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	
	HttpSession session;
	HttpServletRequest request;
	
	String strClientName;
	String strIndustryName;
	String strDesigName;
	String strDepartName;
	String strWLocName;
	String strClientBrandName;
	
	public String execute() {
		session = request.getSession();
	
		if(getStrClientName() != null) {
			checkClientName();
			
		} else if(getStrIndustryName() != null) {
			checkIndustryName();
			
		} else if(getStrDesigName() != null) {
			checkClientDesignationName();
			
		} else if(getStrDepartName() != null) {
			checkClientDepartmentName();
			
		} else if(getStrWLocName() != null) {
			checkClientLocationName();
		} else if(getStrClientBrandName() != null) {
			checkClientBrandName();
			
		}
		
		return SUCCESS;
	}

	
	private void checkClientDesignationName() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		//	request.setAttribute("PCA", "<span style=\"color:green\">Client name is available.</span>");
         	request.setAttribute("PCA", "<span style=\"color:green\"></span>");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_designations where client_desig_name = ?");
			pst.setString(1, getStrDesigName());
			rs = pst.executeQuery();
			String strPCA = "";
			if(rs.next()) {
				strPCA = "<span style=\"color:red\">This Designation name is already available in the system.<br/>Please try with other.</span>";
			}
			rs.close();
			pst.close();
//			System.out.println("strPCA ===>> " + strPCA.length());
			request.setAttribute("PCA", strPCA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void checkClientDepartmentName() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		//	request.setAttribute("PCA", "<span style=\"color:green\">Client name is available.</span>");
         	request.setAttribute("PCA", "<span style=\"color:green\"></span>");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_departments where client_depart_name = ?");
			pst.setString(1, getStrDepartName());
			rs = pst.executeQuery();
			String strPCA = "";
			if(rs.next()) {
				strPCA = "<span style=\"color:red\">This Department name is already available in the system.<br/>Please try with other.</span>";
			}
			rs.close();
			pst.close();
//			System.out.println("strPCA ===>> " + strPCA.length());
			request.setAttribute("PCA", strPCA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void checkClientLocationName() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		//	request.setAttribute("PCA", "<span style=\"color:green\">Client name is available.</span>");
         	request.setAttribute("PCA", "<span style=\"color:green\"></span>");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_locations where client_loc_name = ?");
			pst.setString(1, getStrWLocName());
			rs = pst.executeQuery();
			String strPCA = "";
			if(rs.next()) {
				strPCA = "<span style=\"color:red\">This Location name is already available in the system.<br/>Please try with other.</span>";
			}
			rs.close();
			pst.close();
//			System.out.println("strPCA ===>> " + strPCA.length());
			request.setAttribute("PCA", strPCA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void checkIndustryName() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		//	request.setAttribute("PCA", "<span style=\"color:green\">Client name is available.</span>");
         	request.setAttribute("PCA", "<span style=\"color:green\"></span>");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_industry_details where industry_name = ?");
			pst.setString(1, getStrIndustryName());
			rs = pst.executeQuery();
			String strPCA = "";
			if(rs.next()) {
				strPCA = "<span style=\"color:red\">This Industry name is already available in the system.<br/>Please try with other.</span>";
			}
			rs.close();
			pst.close();
//			System.out.println("strPCA ===>> " + strPCA.length());
			request.setAttribute("PCA", strPCA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void checkClientName() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
		//	request.setAttribute("PCA", "<span style=\"color:green\">Client name is available.</span>");
         	request.setAttribute("PCA", "<span style=\"color:green\"></span>");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_details where client_name = ?");
			pst.setString(1, getStrClientName());
			rs = pst.executeQuery();
			String strPCA="";
			if(rs.next()){
				strPCA = "<span style=\"color:red\">This Client name is already available in the system.<br/>Please choose different code.</span>";
			}
			rs.close();
			pst.close();
//			System.out.println("strPCA ===>> " + strPCA.length());
			request.setAttribute("PCA", strPCA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void checkClientBrandName() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
		//	request.setAttribute("PCA", "<span style=\"color:green\">Client name is available.</span>");
         	request.setAttribute("PCA", "<span style=\"color:green\"></span>");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_brand_details where client_brand_name = ?");
			pst.setString(1, getStrClientBrandName());
			rs = pst.executeQuery();
			String strPCA="";
			if(rs.next()){
				strPCA = "<span style=\"color:red\">This Client Subsidiary/ Brand name is already available in the system.<br/>Please choose different code.</span>";
			}
			rs.close();
			pst.close();
//			System.out.println("strPCA ===>> " + strPCA.length());
			request.setAttribute("PCA", strPCA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getStrClientName() {
		return strClientName;
	}

	public void setStrClientName(String strClientName) {
		this.strClientName = strClientName;
	}

	public String getStrIndustryName() {
		return strIndustryName;
	}

	public void setStrIndustryName(String strIndustryName) {
		this.strIndustryName = strIndustryName;
	}

	public String getStrDesigName() {
		return strDesigName;
	}

	public void setStrDesigName(String strDesigName) {
		this.strDesigName = strDesigName;
	}

	public String getStrDepartName() {
		return strDepartName;
	}

	public void setStrDepartName(String strDepartName) {
		this.strDepartName = strDepartName;
	}

	public String getStrWLocName() {
		return strWLocName;
	}

	public void setStrWLocName(String strWLocName) {
		this.strWLocName = strWLocName;
	}

	public String getStrClientBrandName() {
		return strClientBrandName;
	}

	public void setStrClientBrandName(String strClientBrandName) {
		this.strClientBrandName = strClientBrandName;
	}

}
