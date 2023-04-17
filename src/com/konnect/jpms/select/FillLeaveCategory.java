package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillLeaveCategory {
	String leaveCategoryId;
	String leaveCategoryName;
	
	public FillLeaveCategory() {
		super();
	}
	
	public FillLeaveCategory(String leaveCategoryId, String leaveCategoryName) {
		super();
		this.leaveCategoryId = leaveCategoryId;
		this.leaveCategoryName = leaveCategoryName;
	}

	public String getLeaveCategoryId() {
		return leaveCategoryId;
	}
	public void setLeaveCategoryId(String leaveCategoryId) {
		this.leaveCategoryId = leaveCategoryId;
	}
	public String getLeaveCategoryName() {
		return leaveCategoryName;
	}
	public void setLeaveCategoryName(String leaveCategoryName) {
		this.leaveCategoryName = leaveCategoryName;
	}

	public List<FillLeaveCategory> fillLeaveCategory() {
		List<FillLeaveCategory> al = new ArrayList<FillLeaveCategory>();
		al.add(new FillLeaveCategory("1", "Maternity"));
		al.add(new FillLeaveCategory("2", "Paternity"));
		al.add(new FillLeaveCategory("0", "none"));
		return al;
	}
	
}
