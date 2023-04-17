package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;


public class FillRimbursementType {
	String typeId;
	String typeName;

	

	public FillRimbursementType(String typeId, String typeName) {
		this.typeId = typeId;
		this.typeName = typeName;
	}
	public FillRimbursementType() {
	}
	
	public List<FillRimbursementType> fillRimbursementType(){
		List<FillRimbursementType> al = new ArrayList<FillRimbursementType>();
//		al.add(new FillRimbursementType("Travel", "Travel"));
//		al.add(new FillRimbursementType("Refreshment", "Refreshment"));
//		al.add(new FillRimbursementType("Mobile Bill", "Mobile Bill"));
		al.add(new FillRimbursementType("Conveyance Bill", "Conveyance Bill")); 
		al.add(new FillRimbursementType("Food Expenses", "Food Expenses"));
		al.add(new FillRimbursementType("Supplies", "Supplies"));
		al.add(new FillRimbursementType("Repair and Maintenance", "Repair and Maintenance"));
		al.add(new FillRimbursementType("Accommodation", "Accommodation"));
		al.add(new FillRimbursementType("Internet Charges", "Internet Charges"));
//		al.add(new FillRimbursementType("Alcohol", "Alcohol"));
		al.add(new FillRimbursementType("Relocation Expenses", "Relocation Expenses"));
		al.add(new FillRimbursementType("Courier Charges", "Courier Charges"));
		al.add(new FillRimbursementType("Laundry Charges", "Laundry Charges"));
		al.add(new FillRimbursementType("Printing & Stationery", "Printing & Stationery"));
		al.add(new FillRimbursementType("Others", "Others"));
		
		return al;
	} 
	
	public List<FillRimbursementType> fillRimbursementType1(){
		List<FillRimbursementType> al = new ArrayList<FillRimbursementType>();
		al.add(new FillRimbursementType("T", "Travel Plan"));
//		al.add(new FillRimbursementType("O", "Other"));
		al.add(new FillRimbursementType("L", "Local")); 
		al.add(new FillRimbursementType("M", "Mobile Bill"));
		al.add(new FillRimbursementType("P", "Project"));
		return al;
	}
	
	public List<FillRimbursementType> fillmodeoftravel(){
		List<FillRimbursementType> al = new ArrayList<FillRimbursementType>();
		al.add(new FillRimbursementType("Owned vehical- 2 Wheeler", "Owned vehical- 2 Wheeler"));
		al.add(new FillRimbursementType("Owned vehical- 4 Wheeler", "Owned vehical- 4 Wheeler"));
		al.add(new FillRimbursementType("Bus", "Bus"));
		al.add(new FillRimbursementType("Train", "Train"));
		al.add(new FillRimbursementType("Taxi", "Taxi"));
		al.add(new FillRimbursementType("Auto", "Auto"));
//		al.add(new FillRimbursementType("Air", "Air"));
		al.add(new FillRimbursementType("Two Wheeler", "Two Wheeler"));
		al.add(new FillRimbursementType("Other", "Other"));
		
		return al;
	}
	
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}	
	
}
