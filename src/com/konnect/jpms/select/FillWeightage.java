package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillWeightage {

	String weightageId;
	String weightageName;
	
	private FillWeightage(String weightageId, String weightageName) {
		this.weightageId = weightageId;
		this.weightageName = weightageName;
	}
	
	public FillWeightage() {
	}
	
	public List<FillWeightage> fillWeightage(){
		
		List<FillWeightage> al = new ArrayList<FillWeightage>();
		
		al.add(new FillWeightage("1", "1"));
		al.add(new FillWeightage("2", "2"));
		al.add(new FillWeightage("3", "3"));
		al.add(new FillWeightage("4", "4"));
		al.add(new FillWeightage("5", "5"));
		al.add(new FillWeightage("6", "6"));
		al.add(new FillWeightage("7", "7"));
		al.add(new FillWeightage("8", "8"));
		al.add(new FillWeightage("9", "9"));
		al.add(new FillWeightage("10", "10"));
		
		return al;
	}

	public String getWeightageId() {
		return weightageId;
	}

	public void setWeightageId(String weightageId) {
		this.weightageId = weightageId;
	}

	public String getWeightageName() {
		return weightageName;
	}

	public void setWeightageName(String weightageName) {
		this.weightageName = weightageName;
	}
	
	
}
