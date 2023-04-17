package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillTimeType {
  
	String timeTypeId;
	String timeTypeName;
	
	public String getTimeTypeId() {
		return timeTypeId;
	}
	public void setTimeTypeId(String timeTypeId) {
		this.timeTypeId = timeTypeId;
	}
	public String getTimeTypeName() {
		return timeTypeName;
	}
	public void setTimeTypeName(String timeTypeName) {
		this.timeTypeName = timeTypeName;
	}
	
	public FillTimeType(String timeTypeId, String timeTypeName) {
		this.timeTypeId = timeTypeId;
		this.timeTypeName = timeTypeName;
	}
	
	public FillTimeType() {
	}
	
	public List<FillTimeType> fillTimeType(){
		List<FillTimeType> al = new ArrayList<FillTimeType>();
	
		try {

//				al.add(new FillTimeType("TARDY", "Tardy"));
				al.add(new FillTimeType("LATE", "Late"));
				al.add(new FillTimeType("EARLY", "Early"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
}
