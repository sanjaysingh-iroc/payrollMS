package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillBloodGroup {

	String bloodGroupId;
	String bloodGroupName;
	
	private FillBloodGroup(String bloodGroupId, String bloodGroupName) {
		this.bloodGroupId = bloodGroupId;
		this.bloodGroupName = bloodGroupName;
	}
	
	public FillBloodGroup() {
	}
	
	public List<FillBloodGroup> fillBloodGroup(){
		List<FillBloodGroup> al = new ArrayList<FillBloodGroup>();
	
		try {

			al.add(new FillBloodGroup("A+", "A+"));
			al.add(new FillBloodGroup("A-", "A-"));
			al.add(new FillBloodGroup("B+", "B+"));
			al.add(new FillBloodGroup("B-", "B-"));
			al.add(new FillBloodGroup("AB+", "AB+"));
			al.add(new FillBloodGroup("AB-", "AB-"));
			al.add(new FillBloodGroup("O+", "O+"));
			al.add(new FillBloodGroup("O-", "O-"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getBloodGroupId() {
		return bloodGroupId;
	}

	public void setBloodGroupId(String bloodGroupId) {
		this.bloodGroupId = bloodGroupId;
	}

	public String getBloodGroupName() {
		return bloodGroupName;
	}

	public void setBloodGroupName(String bloodGroupName) {
		this.bloodGroupName = bloodGroupName;
	}
}  
