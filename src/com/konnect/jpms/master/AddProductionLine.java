package com.konnect.jpms.master;

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

public class AddProductionLine extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;  
	String strSessionEmpId; 
	
	String strOrg;
	String productionLineId;
	String productionLineCode;
	String productionLineName;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId =  (String)session.getAttribute(EMPID);	
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if (operation != null && operation.equals("D")) {
			return deleteProductionLine(uF, strId);
		}
		if (operation != null && operation.equals("E")) {
			return viewProductionLine(uF, strId);
		}
		
		if (uF.parseToInt(getProductionLineId()) >0) {
			return updateProductionLine(uF);
		}
		if (getProductionLineCode() != null && getProductionLineCode().length()>0) {
			return insertProductionLine(uF);
		}
		return LOAD;
	}

	public String insertProductionLine(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into production_line_details(production_line_code,production_line_name,org_id,added_by,added_date)" +
					"values(?,?,?,?, ?)");
			pst.setString(1, getProductionLineCode());
			pst.setString(2, getProductionLineName());
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getProductionLineName()+" saved successfully."+END);
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

	
	public String viewProductionLine(UtilityFunctions uF, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from production_line_details where production_line_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();			
			while(rs.next()){
				setProductionLineId(rs.getString("production_line_id"));
				setProductionLineCode(rs.getString("production_line_code"));
				setProductionLineName(rs.getString("production_line_name"));
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
	
	
	public String updateProductionLine(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update production_line_details set production_line_code=?,production_line_name=?,added_by=?," +
					"added_date=? where production_line_id=?");
			pst.setString(1, getProductionLineCode());
			pst.setString(2, getProductionLineName());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(getProductionLineId()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getProductionLineName()+" updated successfully."+END);
			
		} catch (Exception e) {
			request.setAttribute(MESSAGE, "Error in updation");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;		
	}

	public String deleteProductionLine(UtilityFunctions uF, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from production_line_details where production_line_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
		} catch (Exception e) {
			request.setAttribute(MESSAGE, "Error in deletion");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getProductionLineId() {
		return productionLineId;
	}

	public void setProductionLineId(String productionLineId) {
		this.productionLineId = productionLineId;
	}

	public String getProductionLineCode() {
		return productionLineCode;
	}

	public void setProductionLineCode(String productionLineCode) {
		this.productionLineCode = productionLineCode;
	}

	public String getProductionLineName() {
		return productionLineName;
	}

	public void setProductionLineName(String productionLineName) {
		this.productionLineName = productionLineName;
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