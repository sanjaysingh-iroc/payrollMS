package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillClassAndDivisionLevel {

	
	String classOrDivId;
	String classOrDivName;
	
	public FillClassAndDivisionLevel(String classOrDivId, String classOrDivName) {
		this.classOrDivId = classOrDivId;
		this.classOrDivName = classOrDivName;
	}
	
	public FillClassAndDivisionLevel() {}
	
	public List<FillClassAndDivisionLevel> fillClassLevel(){
		List<FillClassAndDivisionLevel> al = new ArrayList<FillClassAndDivisionLevel>();
	
		try {
			al.add(new FillClassAndDivisionLevel("1", "1"));
			al.add(new FillClassAndDivisionLevel("2", "2"));
			al.add(new FillClassAndDivisionLevel("3", "3"));
			al.add(new FillClassAndDivisionLevel("4", "4"));
			al.add(new FillClassAndDivisionLevel("5", "5"));
			al.add(new FillClassAndDivisionLevel("6", "6"));
			al.add(new FillClassAndDivisionLevel("7", "7"));
			al.add(new FillClassAndDivisionLevel("8", "8"));
			al.add(new FillClassAndDivisionLevel("9", "9"));
			al.add(new FillClassAndDivisionLevel("10", "10"));
			al.add(new FillClassAndDivisionLevel("11", "11"));
			al.add(new FillClassAndDivisionLevel("12", "12"));
			al.add(new FillClassAndDivisionLevel("13", "13"));
			al.add(new FillClassAndDivisionLevel("14", "14"));
			al.add(new FillClassAndDivisionLevel("15", "15"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public List<FillClassAndDivisionLevel> fillDivisionLevel(){
		List<FillClassAndDivisionLevel> al = new ArrayList<FillClassAndDivisionLevel>();
	
		try {
			al.add(new FillClassAndDivisionLevel("1", "1"));
			al.add(new FillClassAndDivisionLevel("2", "2"));
			al.add(new FillClassAndDivisionLevel("3", "3"));
			al.add(new FillClassAndDivisionLevel("4", "4"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	
	public String getClassOrDivId() {
		return classOrDivId;
	}

	public void setClassOrDivId(String classOrDivId) {
		this.classOrDivId = classOrDivId;
	}

	public String getClassOrDivName() {
		return classOrDivName;
	}

	public void setClassOrDivName(String classOrDivName) {
		this.classOrDivName = classOrDivName;
	}


}  
