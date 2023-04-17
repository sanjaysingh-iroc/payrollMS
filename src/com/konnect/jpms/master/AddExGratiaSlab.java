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

public class AddExGratiaSlab extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;

	String gratiaSlabId;
	String exGratiaSlab;
	String slabFrom;
	String slabTo;
	String slabPercentage;
	
	String userscreen;
	String navigationId;
	String toPage;
	String toTab;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		if (operation!=null && operation.equals("D")) {
			return deleteExGratia(strId);  
		}
		if (operation!=null && operation.equals("E")) { 
			return viewExGratia(strId);
		} 
		
		if (getGratiaSlabId()!=null && getGratiaSlabId().length()>0) {
			return updateExGratia();
		}
		
		if(getExGratiaSlab()!=null && getExGratiaSlab().length()>0){
			return insertExGratia();
		}
		
		return LOAD;
		
	}

	public String insertExGratia() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database(); 
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into EX_GRATIA_SLAB_DETAILS (EX_GRATIA_SLAB,SLAB_FROM,SLAB_TO,SLAB_PERCENTAGE,ENTRY_DATE,ADDED_BY) values(?,?,?,?,?,?)");
			pst.setString(1, getExGratiaSlab());
			pst.setDouble(2, uF.parseToDouble(getSlabFrom()));
			pst.setDouble(3, uF.parseToDouble(getSlabTo()));
			pst.setDouble(4, uF.parseToDouble(getSlabPercentage()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Saved successfully."+END);
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Save failed. Please try again."+END);
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String viewExGratia(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from EX_GRATIA_SLAB_DETAILS where GRATIA_SLAB_ID=?");
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			while(rs.next()){
				setGratiaSlabId(rs.getString("GRATIA_SLAB_ID"));
				setExGratiaSlab(rs.getString("EX_GRATIA_SLAB"));
				setSlabFrom(rs.getString("SLAB_FROM"));
				setSlabTo(rs.getString("SLAB_TO"));
				setSlabPercentage(rs.getString("SLAB_PERCENTAGE"));
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
		return UPDATE;

	}
	
	public String updateExGratia() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update EX_GRATIA_SLAB_DETAILS set EX_GRATIA_SLAB=?,SLAB_FROM=?,SLAB_TO=?,SLAB_PERCENTAGE=?,ENTRY_DATE=?,ADDED_BY=? where GRATIA_SLAB_ID=?");
			pst.setString(1, getExGratiaSlab());
			pst.setDouble(2, uF.parseToDouble(getSlabFrom()));
			pst.setDouble(3, uF.parseToDouble(getSlabTo()));
			pst.setDouble(4, uF.parseToDouble(getSlabPercentage()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setInt(7, uF.parseToInt(getGratiaSlabId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Updated successfully."+END);
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Update falied. Please try again."+END);
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteExGratia(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from EX_GRATIA_SLAB_DETAILS where GRATIA_SLAB_ID=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Delete failed. Please try again."+END);
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

	public String getGratiaSlabId() {
		return gratiaSlabId;
	}

	public void setGratiaSlabId(String gratiaSlabId) {
		this.gratiaSlabId = gratiaSlabId;
	}

	public String getExGratiaSlab() {
		return exGratiaSlab;
	}

	public void setExGratiaSlab(String exGratiaSlab) {
		this.exGratiaSlab = exGratiaSlab;
	}

	public String getSlabFrom() {
		return slabFrom;
	}

	public void setSlabFrom(String slabFrom) {
		this.slabFrom = slabFrom;
	}

	public String getSlabTo() {
		return slabTo;
	}

	public void setSlabTo(String slabTo) {
		this.slabTo = slabTo;
	}

	public String getSlabPercentage() {
		return slabPercentage;
	}

	public void setSlabPercentage(String slabPercentage) {
		this.slabPercentage = slabPercentage;
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

	public String getToTab() {
		return toTab;
	}

	public void setToTab(String toTab) {
		this.toTab = toTab;
	}

}
