package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillMaritalStatus {

	
	String maritalStatusId;
	String maritalStatusName;
	
	private FillMaritalStatus(String maritalStatusId, String maritalStatusName) {
		this.maritalStatusId = maritalStatusId;
		this.maritalStatusName = maritalStatusName;
	}
	
	public FillMaritalStatus() {
	}
	
	public List<FillMaritalStatus> fillMaritalStatus() {
		List<FillMaritalStatus> al = new ArrayList<FillMaritalStatus>();
	
		try {
			al.add(new FillMaritalStatus("U", "Unmarried"));
			al.add(new FillMaritalStatus("M", "Married"));
			al.add(new FillMaritalStatus("D", "Divorced")); 
			al.add(new FillMaritalStatus("W", "Widow")); 
//			al.add(new FillMaritalStatus("O", "Others")); 
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getMaritalStatusId() {
		return maritalStatusId;
	}

	public void setMaritalStatusId(String maritalStatusId) {
		this.maritalStatusId = maritalStatusId;
	}

	public String getMaritalStatusName() {
		return maritalStatusName;
	}

	public void setMaritalStatusName(String maritalStatusName) {
		this.maritalStatusName = maritalStatusName;
	}
}  
