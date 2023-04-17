package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillDegreeDuration {

	
	private int degreeDurationID;
	private String degreeDurationName;
	
	private FillDegreeDuration(int probationID, String probationName) {
		this.degreeDurationID = probationID;
		this.degreeDurationName = probationName;
	}
	
	public FillDegreeDuration() {
	}
	
	public List<FillDegreeDuration> fillDegreeDuration(){
		List<FillDegreeDuration> al = new ArrayList<FillDegreeDuration>();
	
		try {
			
			al.add(new FillDegreeDuration(1, "1 Year"));
			al.add(new FillDegreeDuration(2, "2 Years"));
			al.add(new FillDegreeDuration(3, "3 Years"));
			al.add(new FillDegreeDuration(4, "4 Years"));
			al.add(new FillDegreeDuration(5, "5 Years"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public int getDegreeDurationID() {
		return degreeDurationID;
	}

	public void setDegreeDurationID(int degreeDurationID) {
		this.degreeDurationID = degreeDurationID;
	}

	public String getDegreeDurationName() {
		return degreeDurationName;
	}

	public void setDegreeDurationName(String degreeDurationName) {
		this.degreeDurationName = degreeDurationName;
	}


}  
