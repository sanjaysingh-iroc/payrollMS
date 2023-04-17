package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DesigValidation extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String desigName;
	private String strLevel;
	
	public String execute() throws Exception {
		 
		checkDesignationValidator();
		return SUCCESS;
	}

	
	public void checkDesignationValidator(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select designation_name from designation_details where designation_name=? and level_id=?");
			pst.setString(1, desigName);
			pst.setInt(2, uF.parseToInt(getStrLevel()));
			rs=pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
//				System.out.println("pst =====> "+pst);
			if(rs.next()){
				request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This Designation Exists.Kindly type Different Designation.</font></b>");
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
	
	public String getDesigName() {
		return desigName;
	}

	public void setDesigName(String desigName) {
		this.desigName = desigName;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
