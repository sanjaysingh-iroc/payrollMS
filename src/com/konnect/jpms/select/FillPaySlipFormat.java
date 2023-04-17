package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillPaySlipFormat {

	
	String paySlipFormatId;
	String paySlipFormatName;


	public FillPaySlipFormat(String paySlipFormatId, String paySlipFormatName) {
		this.paySlipFormatId = paySlipFormatId;
		this.paySlipFormatName = paySlipFormatName;
	}
	
	public FillPaySlipFormat() {
	}
	
	public List<FillPaySlipFormat> fillPaySlipFormat(){
		List<FillPaySlipFormat> al = new ArrayList<FillPaySlipFormat>();
	
		try {

			al.add(new FillPaySlipFormat("1", "Format 1"));
			al.add(new FillPaySlipFormat("2", "Format 2"));
			al.add(new FillPaySlipFormat("3", "Format 3"));
			al.add(new FillPaySlipFormat("4", "Format 4"));
			al.add(new FillPaySlipFormat("5", "Format 5"));
			al.add(new FillPaySlipFormat("6", "Format 6"));
			al.add(new FillPaySlipFormat("7", "Format 7"));	
			al.add(new FillPaySlipFormat("8", "Format 8"));		//added by parvez date: 02-09-2022
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return al;
		
	}

	public String getPaySlipFormatId() {
		return paySlipFormatId;
	}

	public void setPaySlipFormatId(String paySlipFormatId) {
		this.paySlipFormatId = paySlipFormatId;
	}

	public String getPaySlipFormatName() {
		return paySlipFormatName;
	}

	public void setPaySlipFormatName(String paySlipFormatName) {
		this.paySlipFormatName = paySlipFormatName;
	}
}