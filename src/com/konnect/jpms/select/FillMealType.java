package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;
import com.konnect.jpms.util.IConstants;

public class FillMealType {

	private String mealTypeId;
	private String mealTypeName;
	
	public FillMealType() {
	}
	
	public FillMealType(String mealTypeId, String mealTypeName) {
		this.mealTypeId = mealTypeId;
		this.mealTypeName = mealTypeName;
	}
	public List<FillMealType> fillMealType(){
		List<FillMealType> al = new ArrayList<FillMealType>();
	
		try {

				al.add(new FillMealType(IConstants.BREAKFAST,"Breakfast"));
				al.add(new FillMealType(IConstants.LUNCH, "Lunch"));
				al.add(new FillMealType(IConstants.DINNER, "Dinner"));
				al.add(new FillMealType(IConstants.OTHER, "Other"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getMealTypeId() {
		return mealTypeId;
	}

	public void setMealTypeId(String mealTypeId) {
		this.mealTypeId = mealTypeId;
	}

	public String getMealTypeName() {
		return mealTypeName;
	}

	public void setMealTypeName(String mealTypeName) {
		this.mealTypeName = mealTypeName;
	}
	
	
}
