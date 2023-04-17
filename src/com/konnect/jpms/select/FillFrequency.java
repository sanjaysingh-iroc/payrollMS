package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillFrequency {

	String frequencyTypeId;
	String frequencyTypeName;
	
	public String getFrequencyTypeId() {
		return frequencyTypeId;
	}

	public void setFrequencyTypeId(String frequencyTypeId) {
		this.frequencyTypeId = frequencyTypeId;
	}

	public String getFrequencyTypeName() {
		return frequencyTypeName;
	}

	public void setFrequencyTypeName(String frequencyTypeName) {
		this.frequencyTypeName = frequencyTypeName;
	}
	
	public FillFrequency(String frequencyTypeId, String frequencyTypeName) {
		this.frequencyTypeId = frequencyTypeId;
		this.frequencyTypeName = frequencyTypeName;
	}
	
	public FillFrequency() {
	}
	
	public List<FillFrequency> fillFrequency(){
		List<FillFrequency> al = new ArrayList<FillFrequency>();
	
		try {

				al.add(new FillFrequency("M", "Monthly"));
				al.add(new FillFrequency("F", "Fortnightly"));
				al.add(new FillFrequency("W", "Weekly"));
				al.add(new FillFrequency("D", "Daily"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	
	
}
