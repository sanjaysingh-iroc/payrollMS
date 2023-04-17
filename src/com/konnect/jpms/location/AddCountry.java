package com.konnect.jpms.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCountry extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	String country;
	String countryId;
	String country1;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, PAddCountry);
		String operation = request.getParameter("operation");
		if (operation.equals("D")) {
			return deleteCountry();
		}
		if (operation.equals("U")) {
				return updateCountry();
			}
		if (operation.equals("A")) {
				return insertCountry();
			}
		return SUCCESS;
	}

	public void validate() {
		
        if (getCountry()!=null && getCountry().length() == 0) {
            addFieldError("country", " Country is required");
        } 
        loadValidateCountry();
    }

	public String loadValidateCountry() {
		request.setAttribute(PAGE, PAddCountry);
		request.setAttribute(TITLE, TAddCountry);
		return LOAD;
	}
	
	public String updateCountry() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		switch(columnId) {
			case 0 : columnName = "country_name"; break;
		}
		String updateCountry = "UPDATE country SET "+columnName+"=? WHERE country_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateCountry);
			pst.setString(1, request.getParameter("value"));
			pst.setInt(2, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();		
			request.setAttribute(MESSAGE, "Error in updation");
			return ERROR;
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
		
	}
	
	public String insertCountry() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertCountry);
			pst.setString(1, getCountry());
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, getCountry() + " added successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
			return ERROR;
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteCountry() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteCountry);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			request.setAttribute(MESSAGE, "Deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
			return ERROR;
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
		
	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		this.request.setAttribute(PAGE, PAddCountry);
		
		System.out.println("=====> Setting request===>");
	}

	public String getCountry1() {
		return country1;
	}

	public void setCountry1(String country1) {
		this.country1 = country1;
	}

}



//<interceptor-ref name="token" />
//<interceptor-ref name="basicStack"/>			
//<result name="invalid.token">/jsp/common/viewPage.jsp</result>