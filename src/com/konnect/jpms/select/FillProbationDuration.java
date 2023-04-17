package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillProbationDuration {

	
	int probationDurationID;
	String probationDurationName;
	
	private FillProbationDuration(int probationID, String probationName) {
		this.probationDurationID = probationID;
		this.probationDurationName = probationName;
	}
	
	public FillProbationDuration() {
	}
	
	public List<FillProbationDuration> fillProbationDuration(){
		List<FillProbationDuration> al = new ArrayList<FillProbationDuration>();
	
		try {
				for(int i=0; i<=180; i++){
					al.add(new FillProbationDuration(i, i+" Day" +((i>1)?"s":"")));
				}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public int getProbationDurationID() {
		return probationDurationID;
	}

	public void setProbationDurationID(int probationDurationID) {
		this.probationDurationID = probationDurationID;
	}

	public String getProbationDurationName() {
		return probationDurationName;
	}

	public void setProbationDurationName(String probationDurationName) {
		this.probationDurationName = probationDurationName;
	}


}  
