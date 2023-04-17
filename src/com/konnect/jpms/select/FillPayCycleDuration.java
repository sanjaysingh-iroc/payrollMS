package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillPayCycleDuration {

	
	String paycycleDurationId;
	String paycycleDurationName;
	
	

	
	public FillPayCycleDuration(String paycycleDurationId, String paycycleDurationName) {
		this.paycycleDurationId = paycycleDurationId;
		this.paycycleDurationName = paycycleDurationName;
	}
	
	public FillPayCycleDuration() {
	}
	
	public List<FillPayCycleDuration> fillPayCycleDuration(){
		List<FillPayCycleDuration> al = new ArrayList<FillPayCycleDuration>();
	
		try {
			
			al.add(new FillPayCycleDuration("M", "Monthly"));
			al.add(new FillPayCycleDuration("W", "Weekly"));
			al.add(new FillPayCycleDuration("BW", "Biweekly"));
			al.add(new FillPayCycleDuration("F", "Fortnightly"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getPaycycleDurationId() {
		return paycycleDurationId;
	}

	public void setPaycycleDurationId(String paycycleDurationId) {
		this.paycycleDurationId = paycycleDurationId;
	}

	public String getPaycycleDurationName() {
		return paycycleDurationName;
	}

	public void setPaycycleDurationName(String paycycleDurationName) {
		this.paycycleDurationName = paycycleDurationName;
	}
	
} 
