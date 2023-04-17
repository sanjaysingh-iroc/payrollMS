package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillColour;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddLeaveBreakType extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;

	HttpSession session;
	String orgId;  
	String strLocation;
	
	public String execute() throws Exception {
 
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
	
		session = request.getSession();
		colourList = new FillColour(request).fillColour();
		
		if (operation!=null && operation.equals("D")) {
			return deleteLeaveType(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewWlocationType(strId);
		}
		if (getBreakTypeId()!=null && getBreakTypeId().length()>0) { 
			return updateLeaveType();
		}
		if(getBreakType()!=null && getBreakType().length()>0){
			return insertLeaveType();
		}
		 
		
		return LOAD;
	}

	public String loadLeaveType() {
		request.setAttribute(PAGE, PAddLeaveBreakType);
		request.setAttribute(TITLE, TAddLeaveBreakType);
		
		setBreakType("");
		setBreakTypeId("");
		return LOAD;
	}

	
	public String viewWlocationType(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from leave_break_type where break_type_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setBreakType(rs.getString("break_type_name"));
				setBreakCode(rs.getString("break_type_code"));
				setBreakTypeId(rs.getString("break_type_id"));
				setOrgId(rs.getString("org_id"));
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
	
	public String updateLeaveType() {

		Connection con = null;
		PreparedStatement pst =null;;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateBreakType);
			pst.setString(1, getBreakType());
			pst.setString(2, getBreakCode());
			pst.setString(3, getStrColour());
			pst.setInt(4, uF.parseToInt(getBreakTypeId()));
			
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+getBreakType()+" updated successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String insertLeaveType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertBreakType);
			pst.setString(1, getBreakType());
			pst.setString(2, getBreakCode());
			pst.setString(3, getStrColour());
			pst.setInt(4, uF.parseToInt(getOrgId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+getBreakType()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteLeaveType(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteLeaveBreakType);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String strColour;
	String breakType;
	String breakCode;
	String breakTypeId;
	List<FillColour> colourList;

	public void validate() {
       
    }
	

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		this.request.setAttribute(PAGE, PAddLeaveType);
		
	}

	public String getStrColour() {
		return strColour;
	}

	public void setStrColour(String strColour) {
		this.strColour = strColour;
	}

	public List<FillColour> getColourList() {
		return colourList;
	}
	public void setColourList(List<FillColour> colourList) {
		this.colourList = colourList;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}



	public String getBreakType() {
		return breakType;
	}



	public void setBreakType(String breakType) {
		this.breakType = breakType;
	}



	public String getBreakCode() {
		return breakCode;
	}



	public void setBreakCode(String breakCode) {
		this.breakCode = breakCode;
	}



	public String getBreakTypeId() {
		return breakTypeId;
	}



	public void setBreakTypeId(String breakTypeId) {
		this.breakTypeId = breakTypeId;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}
	
}
