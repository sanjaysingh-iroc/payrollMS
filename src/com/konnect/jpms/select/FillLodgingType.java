package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class FillLodgingType {
	String lodgingTypeId;
	String lodgingTypeName;
	
	public FillLodgingType(String lodgingTypeId, String lodgingTypeName) {
		this.lodgingTypeId = lodgingTypeId;
		this.lodgingTypeName = lodgingTypeName;
	}
	HttpServletRequest request;
	public FillLodgingType(HttpServletRequest request) {
		this.request = request; 
	}
	public FillLodgingType() {
	}
	
	public String getLodgingTypeId() {
		return lodgingTypeId;
	}
	public void setLodgingTypeId(String lodgingTypeId) {
		this.lodgingTypeId = lodgingTypeId;
	}
	public String getLodgingTypeName() {
		return lodgingTypeName;
	}
	public void setLodgingTypeName(String lodgingTypeName) {
		this.lodgingTypeName = lodgingTypeName;
	}
	
	public List<FillLodgingType> fillLodgingType() {
		List<FillLodgingType> al = new ArrayList<FillLodgingType>();

		try {
			
			al.add(new FillLodgingType("1", "2 Star- Single Occupancy"));
			al.add(new FillLodgingType("2", "2 Star- Double Occupancy"));
			al.add(new FillLodgingType("3", "3 Star- Single Occupancy"));
			al.add(new FillLodgingType("4", "3 Star- Double Occupancy"));
			al.add(new FillLodgingType("5", "4 Star- Single Occupancy"));  
			al.add(new FillLodgingType("6", "4 Star- Double Occupancy"));
			al.add(new FillLodgingType("7", "5 Star- Single Occupancy"));
			al.add(new FillLodgingType("8", "5 Star- Double Occupancy"));
			al.add(new FillLodgingType("9", "Service Apartment"));  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
}
