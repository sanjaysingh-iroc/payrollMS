package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillHalfDaySession {

	
	String strHaldDayId;
	String strHaldDayName;
	
	private FillHalfDaySession(String strHaldDayId, String strHaldDayName) {
		this.strHaldDayId = strHaldDayId;
		this.strHaldDayName = strHaldDayName;
	}
	
	public FillHalfDaySession() {
	}
	
	public List<FillHalfDaySession> fillHalfDaySession(){
		List<FillHalfDaySession> al = new ArrayList<FillHalfDaySession>();
	
		try {

			al.add(new FillHalfDaySession("1st Session", "1st Session"));
			al.add(new FillHalfDaySession("2nd Session", "2nd Session"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getStrHaldDayName() {
		return strHaldDayName;
	}

	public void setStrHaldDayName(String strHaldDayName) {
		this.strHaldDayName = strHaldDayName;
	}

	public String getStrHaldDayId() {
		return strHaldDayId;
	}

	public void setStrHaldDayId(String strHaldDayId) {
		this.strHaldDayId = strHaldDayId;
	}


}  
