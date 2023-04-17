package com.konnect.jpms.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.IStatements;

public class FillBillingType implements IStatements{

	String billingName;
	String billingId;
	
	
	
	public FillBillingType(String billingId, String billingName) {
		this.billingId = billingId;
		this.billingName = billingName;
	}
	
	HttpServletRequest request;
	public FillBillingType(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillBillingType() {
	}
	
	public List<FillBillingType> fillBillingTypeList(){
		List<FillBillingType> al = new ArrayList<FillBillingType>();
				al.add(new FillBillingType("F", "Fixed Rate"));
				al.add(new FillBillingType("H", "Hourly Actuals"));
				al.add(new FillBillingType("D", "Daily Actuals"));
				al.add(new FillBillingType("M", "Monthly Actuals"));
		return al;
	}
	
	
	public List<FillBillingType> fillBillingBasisTypeList(){
		List<FillBillingType> al = new ArrayList<FillBillingType>();
//				al.add(new FillBillingType("F", "Fixed Rate"));
				al.add(new FillBillingType("H", "Hourly"));
				al.add(new FillBillingType("D", "Daily"));
				al.add(new FillBillingType("M", "Monthly"));
		return al;
	}
	
	
	public List<FillBillingType> fillBillingKindList() {
		List<FillBillingType> al = new ArrayList<FillBillingType>();
//				al.add(new FillBillingType("O", "Fixed"));
				al.add(new FillBillingType("O", "One time"));
				al.add(new FillBillingType("W", "Weekly"));
				al.add(new FillBillingType("B", "Biweekly"));
				al.add(new FillBillingType("M", "Monthly"));
				al.add(new FillBillingType("Q", "Quarterly"));
				al.add(new FillBillingType("H", "Half Year"));
				al.add(new FillBillingType("A", "Annually"));
						
		return al;
	}
	
	
	public List<FillBillingType> fillBillingKindListBillTypewise(String billType) {
		List<FillBillingType> al = new ArrayList<FillBillingType>();
		if(billType != null && billType.equals("F")) {
			al.add(new FillBillingType("O", "Milestone based"));
			al.add(new FillBillingType("M", "Monthly"));
			al.add(new FillBillingType("Q", "Quarterly"));
			al.add(new FillBillingType("H", "Half Year"));
			al.add(new FillBillingType("A", "Annually"));
			
		} else if(billType != null && !billType.equals("")) {
			al.add(new FillBillingType("O", "One time"));
			al.add(new FillBillingType("W", "Weekly"));
			al.add(new FillBillingType("B", "Biweekly"));
			al.add(new FillBillingType("M", "Monthly"));
			al.add(new FillBillingType("Q", "Quarterly"));
			al.add(new FillBillingType("H", "Half Year"));
			al.add(new FillBillingType("A", "Annually"));
		}	
		return al;
	}

	public String getBillingName() {
		return billingName;
	}

	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}

	public String getBillingId() {
		return billingId;
	}

	public void setBillingId(String billingId) {
		this.billingId = billingId;
	}
	

	


}
