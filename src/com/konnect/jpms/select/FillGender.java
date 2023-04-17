package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillGender {

	
	String genderId;
	String genderName;
	
	public FillGender(String genderId, String genderName) {
		this.genderId = genderId;
		this.genderName = genderName;
	}
	
	public FillGender() {
	}
	
	public List<FillGender> fillGender(){
		List<FillGender> al = new ArrayList<FillGender>();
	
		try {
			al.add(new FillGender("M", "Male"));
			al.add(new FillGender("F", "Female"));
			al.add(new FillGender("O", "Other"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getGenderId() {
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
	}

	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}
}  
