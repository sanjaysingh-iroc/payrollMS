package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillPayMode {

	
	String payModeId;
	String payModeName;
	
	public String getPayModeId() {
		return payModeId;
	}

	public void setPayModeId(String payModeId) {
		this.payModeId = payModeId;
	}

	public String getPayModeName() {
		return payModeName;
	}

	public void setPayModeName(String payModeName) {
		this.payModeName = payModeName;
	}

	
	public FillPayMode(String payModeId, String payModeName) {
		this.payModeId = payModeId;
		this.payModeName = payModeName;
	}
	
	public FillPayMode() {
	}
	
	public List<FillPayMode> fillPayMode(){
		List<FillPayMode> al = new ArrayList<FillPayMode>();
	
		try {

			al.add(new FillPayMode("X", "Fixed"));
			al.add(new FillPayMode("H", "Hourly"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	public List<FillPayMode> fillPaymentMode(){
		List<FillPayMode> al = new ArrayList<FillPayMode>();
	
		try {

			al.add(new FillPayMode("1", "Bank Transfer"));
			al.add(new FillPayMode("2", "Cash"));
			al.add(new FillPayMode("3", "Cheque"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	public List<FillPayMode> fillPaymentModeExpenses() {
		List<FillPayMode> al = new ArrayList<FillPayMode>();
		try {
			al.add(new FillPayMode("1", "Cash"));
			al.add(new FillPayMode("2", "Debit Card"));
			al.add(new FillPayMode("3", "Credit Card"));
			al.add(new FillPayMode("4", "Cheque"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
}  
