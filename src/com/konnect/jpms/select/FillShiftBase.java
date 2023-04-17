package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillShiftBase {
	String shiftBaseId;
	String shiftBaseType;
	public String getShiftBaseId() {
		return shiftBaseId;
	}
	public void setShiftBaseId(String shiftBaseId) {
		this.shiftBaseId = shiftBaseId;
	}
	public String getShiftBaseType() {
		return shiftBaseType;
	}
	public void setShiftBaseType(String shiftBaseType) {
		this.shiftBaseType = shiftBaseType;
	}
	public FillShiftBase(String shiftBaseId, String shiftBaseType) {
		super();
		this.shiftBaseId = shiftBaseId;
		this.shiftBaseType = shiftBaseType;
	}
	public FillShiftBase() {
		super();
	}
	
	public List<FillShiftBase> fillShiftBase() {
		List<FillShiftBase> al = new ArrayList<FillShiftBase>();
		al.add(new FillShiftBase("1","General"));
		al.add(new FillShiftBase("2","Shift Base"));
		
		return al;
	}
}
