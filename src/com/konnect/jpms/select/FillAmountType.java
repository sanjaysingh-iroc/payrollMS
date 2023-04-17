package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillAmountType {

	
	String amountTypeId;
	String amountTypeName;
	
	private FillAmountType(String amountTypeId, String amountTypeName) {
		this.amountTypeId = amountTypeId;
		this.amountTypeName = amountTypeName;
	}
	
	public FillAmountType() {
	}
	
	public List<FillAmountType> fillAmountType(){
		List<FillAmountType> al = new ArrayList<FillAmountType>();
	
		try {

			al.add(new FillAmountType("%", "Percent"));
			al.add(new FillAmountType("A", "Amount"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	public List<FillAmountType> fillAmountType1(){
		List<FillAmountType> al = new ArrayList<FillAmountType>();
	
		try {

			al.add(new FillAmountType("P", "Percent"));
			al.add(new FillAmountType("A", "Amount"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getAmountTypeId() {
		return amountTypeId;
	}

	public void setAmountTypeId(String amountTypeId) {
		this.amountTypeId = amountTypeId;
	}

	public String getAmountTypeName() {
		return amountTypeName;
	}

	public void setAmountTypeName(String amountTypeName) {
		this.amountTypeName = amountTypeName;
	}
}  
