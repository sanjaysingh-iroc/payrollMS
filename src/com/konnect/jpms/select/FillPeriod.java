package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillPeriod {

	
	String periodId;
	String periodName;
	
	private FillPeriod(String periodId, String periodName) {
		this.periodId = periodId;
		this.periodName = periodName;
	}
	 
	public FillPeriod() {
	}
	
	public List<FillPeriod> fillPeriod(int nSwitch){
		List<FillPeriod> al = new ArrayList<FillPeriod>();
	
		try {

			switch(nSwitch){
			case 1:
				al.add(new FillPeriod("T", "Today"));
				al.add(new FillPeriod("Y", "Yesterday"));
				al.add(new FillPeriod("L1W", "Last 1 Week"));
				al.add(new FillPeriod("L1M", "Last 1 Month"));
				al.add(new FillPeriod("L3M", "Last 3 Months"));
				al.add(new FillPeriod("L6M", "Last 6 Months"));
				al.add(new FillPeriod("L1Y", "Last 1 Year"));
				break;
				 
			case 2:
				al.add(new FillPeriod("L1W", "Last 1 Week"));
				al.add(new FillPeriod("L1M", "Last 1 Month"));
				al.add(new FillPeriod("L3M", "Last 3 Months"));
				al.add(new FillPeriod("L6M", "Last 6 Months"));
				al.add(new FillPeriod("L1Y", "Last 1 Year"));
				al.add(new FillPeriod("L2Y", "Last 2 Years"));
				al.add(new FillPeriod("L5Y", "Last 5 Years"));
				al.add(new FillPeriod("L10Y", "Last 10 Years"));
				break;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getPeriodId() {
		return periodId;
	}

	public void setPeriodId(String periodId) {
		this.periodId = periodId;
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}

	
}  
