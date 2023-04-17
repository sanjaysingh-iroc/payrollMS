package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillNoticeDuration {

	
	int noticeDurationID;
	String noticeDurationName;
	
	private FillNoticeDuration(int noticeID, String noticeName) {
		this.noticeDurationID = noticeID;
		this.noticeDurationName = noticeName;
	}
	
	public FillNoticeDuration() {
	}
	
	public List<FillNoticeDuration> fillNoticeDuration(){
		List<FillNoticeDuration> al = new ArrayList<FillNoticeDuration>();
	
		try {
			
//			for(int i=0; i<=180; i++){
			for(int i=0; i<=365; i++){
				al.add(new FillNoticeDuration(i, i+" Day" +((i>1)?"s":"")));
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public int getNoticeDurationID() {
		return noticeDurationID;
	}

	public void setNoticeDurationID(int noticeDurationID) {
		this.noticeDurationID = noticeDurationID;
	}

	public String getNoticeDurationName() {
		return noticeDurationName;
	}

	public void setNoticeDurationName(String noticeDurationName) {
		this.noticeDurationName = noticeDurationName;
	}


}  
