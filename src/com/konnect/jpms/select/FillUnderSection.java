package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillUnderSection {
	
	String underSectionId;
	String underSectionName;
	
	private FillUnderSection(String underSectionId, String underSectionName) {
		this.underSectionId = underSectionId;
		this.underSectionName = underSectionName;
	}
	
	public FillUnderSection() {
		
	}
	
	public List<FillUnderSection> fillUnderSection(){
		List<FillUnderSection> al = new ArrayList<FillUnderSection>();
	
		try {
//			al.add(new FillUnderSection("1", "17(1)"));
//			al.add(new FillUnderSection("2", "17(2)"));
//			al.add(new FillUnderSection("3", "17(3)"));
//			al.add(new FillUnderSection("4", "10"));
//			al.add(new FillUnderSection("5", "16"));
//			al.add(new FillUnderSection("6", "80C"));
//			al.add(new FillUnderSection("7", "89"));
			al.add(new FillUnderSection("8", "Under VI-A 1"));
			al.add(new FillUnderSection("9", "Under VI-A 2"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	public List<FillUnderSection> fillOtherUnderSection(){
		List<FillUnderSection> al = new ArrayList<FillUnderSection>();
	
		try {
			al.add(new FillUnderSection("1", "17(1)"));
			al.add(new FillUnderSection("2", "17(2)"));
			al.add(new FillUnderSection("3", "17(3)"));
			al.add(new FillUnderSection("4", "10"));
			al.add(new FillUnderSection("5", "16"));
			al.add(new FillUnderSection("6", "80C"));
			al.add(new FillUnderSection("7", "89"));
			al.add(new FillUnderSection("8", "Under VI-A 1"));
			al.add(new FillUnderSection("9", "Under VI-A 2"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getUnderSectionId() {
		return underSectionId;
	}

	public void setUnderSectionId(String underSectionId) {
		this.underSectionId = underSectionId;
	}

	public String getUnderSectionName() {
		return underSectionName;
	}

	public void setUnderSectionName(String underSectionName) {
		this.underSectionName = underSectionName;
	}

	public List<FillUnderSection> fillUnderSection10and16() {
		List<FillUnderSection> al = new ArrayList<FillUnderSection>();
	
		try {
//			al.add(new FillUnderSection("1", "17(1)"));
//			al.add(new FillUnderSection("2", "17(2)"));
//			al.add(new FillUnderSection("3", "17(3)"));
			al.add(new FillUnderSection("4", "10"));
			al.add(new FillUnderSection("5", "16"));
//			al.add(new FillUnderSection("6", "80C"));
//			al.add(new FillUnderSection("7", "89"));
//			al.add(new FillUnderSection("8", "Under VI-A 1"));
//			al.add(new FillUnderSection("9", "Under VI-A 2"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
}
