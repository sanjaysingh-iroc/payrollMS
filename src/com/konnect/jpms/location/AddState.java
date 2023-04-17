package com.konnect.jpms.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddState extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	String stateId;
	String stateName;
	String country;	
	List<FillCountry> countryList;
	
	public String execute() throws Exception {
				
		request.setAttribute(PAGE, PAddState);
		String operation = request.getParameter("operation");
		setStateId(request.getParameter("id"));
		
		if (operation.equals("D")) {
			return deleteState();
		}
		if (operation.equals("U")) {
				return updateState();
		}
		if (operation.equals("A")) {
				return insertState();
		}		
		return SUCCESS;
	}

	public void validate() {
		UtilityFunctions uF = new UtilityFunctions();
		
        if (getStateName()!=null && getStateName().length() == 0) {
            addFieldError("stateName", " State is required");
        }
        
        if (getCountry()!=null && uF.parseToInt(getCountry()) == 0) {
            addFieldError("country", " Country is required");
        }
        
        loadValidateState();
    }
	
	public String loadValidateState() {
		request.setAttribute(PAGE, PAddState);
		request.setAttribute(TITLE, TAddState);
		countryList = new FillCountry(request).fillCountry();
		
		return LOAD;
	}
	
	public String insertState() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertState);
			pst.setString(1, getStateName());
			pst.setInt(2, uF.parseToInt(getCountry()));
			pst.execute();
			pst.close();

			request.setAttribute(MESSAGE, getStateName()+" added successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	public String updateState() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			case 0 : columnName = "state_name"; break;
			case 1 : columnName = "country_id"; break;
		}
		
		String updateState = "UPDATE state SET "+columnName+"=? WHERE state_id=?";
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(updateState);
			if(columnId==1)
				pst.setInt(1, uF.parseToInt(request.getParameter("value")) );
			else
				pst.setString(1, request.getParameter("value"));
			pst.setInt(2, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
		
	}
	

	public String deleteState() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteState);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
