package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillNumbers {

	
	String numberId;
	String numberName;
	
	private FillNumbers(String numberId, String numberName) {
		this.numberId = numberId;
		this.numberName = numberName;
	}
	
	public FillNumbers() {
	}
	
	public List<FillNumbers> fillNumbers(int from, int to){
		List<FillNumbers> al = new ArrayList<FillNumbers>();
	
		try {

			for(int i=from; i<=to; i++){
				al.add(new FillNumbers(i+"", i+""));
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getNumberId() {
		return numberId;
	}

	public void setNumberId(String numberId) {
		this.numberId = numberId;
	}

	public String getNumberName() {
		return numberName;
	}

	public void setNumberName(String numberName) {
		this.numberName = numberName;
	}

}  
