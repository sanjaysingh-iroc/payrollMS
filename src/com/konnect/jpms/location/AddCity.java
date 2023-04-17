package com.konnect.jpms.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCity extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {

		request.setAttribute(PAGE, PAddCity);
		String strEdit = request.getParameter("E");
		String strDelete = request.getParameter("D");

		if (strEdit != null) {

			viewCity(strEdit);
			request.setAttribute(TITLE, TViewCity);
			return SUCCESS;
		} 
		if (strDelete != null) {
			deleteCity(strDelete);
			request.setAttribute(TITLE, TDeleteCity);
			return VIEW;
		}

		if (getCityId() != null && getCityId().length() > 0) {
			updateCity();
			request.setAttribute(TITLE, TEditCity);
			return UPDATE;
		} else if (getCityName() != null && getCityName().length() > 0) {
			insertCity();
			request.setAttribute(TITLE, TAddCity);
		}
		return loadCity();

	}

	public String loadValidateCity() {
		request.setAttribute(PAGE, PAddCity);
		request.setAttribute(TITLE, TAddCity);

		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();

		return LOAD;
	}

	public String loadCity() {
		request.setAttribute(PAGE, PAddCity);
		request.setAttribute(TITLE, TAddCity);

		setCityName("");
		setCityId("");
		
//		setCountry(new CommonFunctions().getDefaultCountryId(con));
//		setState(new CommonFunctions().getDefaultStateId(con));

		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();

		return LOAD;
	}

	public String insertCity() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertCity);
			pst.setString(1, getCityName());
			pst.setInt(2, uF.parseToInt(getState()));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, getCityName() + " added successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	public String updateCity() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(updateCity);
			pst.setString(1, getCityName());
			pst.setInt(2, uF.parseToInt(getState()));
			pst.setInt(3, uF.parseToInt(getCityId()));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, getCityId() + " updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewCity(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectCityV);
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			while (rs.next()) {
				setCityId(rs.getString("city_id"));
				setCityName(rs.getString("city_name"));
				setCountry(rs.getString("country_id"));
				setState(rs.getString("state_id"));
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
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();
		return SUCCESS;

	}

	public String deleteCity(String strDelete) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteCity);
			pst.setInt(1, uF.parseToInt(strDelete));
			pst.execute();
			pst.close();

			request.setAttribute(MESSAGE, "deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String country;
	String state;
	String cityName;
	String cityId;

	List<FillCountry> countryList;
	List<FillState> stateList;

	public void validate() {
		UtilityFunctions uF = new UtilityFunctions();
		
		if (getCityName() != null && getCityName().length() == 0) {
			addFieldError("cityName", " Suburb is required");
		}
		if (getCountry() != null && uF.parseToInt(getCountry()) == 0) {
			addFieldError("country", " Country is required");
		}
		if (getState() != null && uF.parseToInt(getState()) == 0) {
			addFieldError("state", " State is required");
		}

		loadValidateCity();
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}