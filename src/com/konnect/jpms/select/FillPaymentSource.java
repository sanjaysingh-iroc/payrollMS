package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.IStatements;

public class FillPaymentSource implements IStatements{

	String paymentSourceId;
	String paymentSourceName;
	
	public FillPaymentSource(String paymentSourceId, String paymentSourceName) {
		this.paymentSourceId = paymentSourceId;
		this.paymentSourceName = paymentSourceName;
	}
	
	public FillPaymentSource() {
	}
	
	public List<FillPaymentSource> fillPaymentSource(){
		List<FillPaymentSource> al = new ArrayList<FillPaymentSource>();
		
		al.add(new FillPaymentSource("C", "Cash"));
		al.add(new FillPaymentSource("Q", "Cheque"));
		al.add(new FillPaymentSource("L", "LTA"));
		al.add(new FillPaymentSource("B", "Bonus"));
		
		return al;
	}
	
	public List<FillPaymentSource> fillPaymentMode(){
		List<FillPaymentSource> al = new ArrayList<FillPaymentSource>();
		
		al.add(new FillPaymentSource("C", "Cash"));
		al.add(new FillPaymentSource("Q", "Cheque"));
		al.add(new FillPaymentSource("D", "Draft"));
		return al;
	}

	public String getPaymentSourceId() {
		return paymentSourceId;
	}

	public void setPaymentSourceId(String paymentSourceId) {
		this.paymentSourceId = paymentSourceId;
	}

	public String getPaymentSourceName() {
		return paymentSourceName;
	}

	public void setPaymentSourceName(String paymentSourceName) {
		this.paymentSourceName = paymentSourceName;
	} 
	
}