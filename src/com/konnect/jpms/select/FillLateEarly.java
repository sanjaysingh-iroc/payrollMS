package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillLateEarly {

	String leId;
	String leName;
	

	public FillLateEarly() {
	}
	
	public FillLateEarly(String leId, String leName) {
		this.leId = leId;
		this.leName = leName;
	}

	
	public List<FillLateEarly> fillLateEarlyAll() {
		List<FillLateEarly> al = new ArrayList<FillLateEarly>();

		try {
			al.add(new FillLateEarly("E", "Early"));
			al.add(new FillLateEarly("L", "Late"));
			al.add(new FillLateEarly("EW", "Extra Hours"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getLeId() {
		return leId;
	}

	public void setLeId(String leId) {
		this.leId = leId;
	}

	public String getLeName() {
		return leName;
	}

	public void setLeName(String leName) {
		this.leName = leName;
	}

}
