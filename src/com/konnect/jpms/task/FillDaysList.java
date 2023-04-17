package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillDaysList implements IStatements{

	String dayName;
	String dayId;
	
	
	
	public FillDaysList(String dayId, String dayName) {
		this.dayId = dayId;
		this.dayName = dayName;
	}
	
	HttpServletRequest request;
	public FillDaysList(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillDaysList() {
	}
	
	public List<FillDaysList> fillDayList(){
		List<FillDaysList> al = new ArrayList<FillDaysList>();
		
		
				al.add(new FillDaysList("Monday","Monday"));
				al.add(new FillDaysList("Tuesday","Tuesday"));
				al.add(new FillDaysList("Wednesday","Wednesday"));
				al.add(new FillDaysList("Thursday","Thursday"));
				al.add(new FillDaysList("Friday","Friday"));
				al.add(new FillDaysList("Saturday","Saturday"));
				al.add(new FillDaysList("Sunday","Sunday"));
		
		return al;
	}

	public String getDayName() {
		return dayName;
	}

	public void setDayName(String dayName) {
		this.dayName = dayName;
	}

	public String getDayId() {
		return dayId;
	}

	public void setDayId(String dayId) {
		this.dayId = dayId;
	}
	

	


}
