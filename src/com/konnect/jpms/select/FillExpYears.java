package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


public class FillExpYears {

	private String expYearsId;
	private String expYearsName;
	
	public FillExpYears(String  expYearsId, String expYearsName) {
		this.expYearsId = expYearsId;
		this.expYearsName = expYearsName;
	}
	
	HttpServletRequest request;
	public FillExpYears(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillExpYears() {
	}
	
	public List<FillExpYears> fillExpYears(){
		
		List<FillExpYears> al = new ArrayList<FillExpYears>();
		al.add(new FillExpYears("1","0 to 1 Year"));
		al.add(new FillExpYears("2","1 to 2 Years"));
		al.add(new FillExpYears("3","2 to 5 Years"));
		al.add(new FillExpYears("4","5 to 10 Years"));
		al.add(new FillExpYears("5","10+ Years"));
		
		return al;
	}

	public String getExpYearsId() {
		return expYearsId;
	}

	public void setExpYearsId(String expYearsId) {
		this.expYearsId = expYearsId;
	}

	public String getExpYearsName() {
		return expYearsName;
	}

	public void setExpYearsName(String expYearsName) {
		this.expYearsName = expYearsName;
	}
	
	
}  
