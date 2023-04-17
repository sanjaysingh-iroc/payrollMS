package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillMonth {

	String monthId;
	String monthName;

	

	public FillMonth(String monthId, String monthName) {
		this.monthId = monthId;
		this.monthName = monthName;
	}

	public FillMonth() {
	}

	public List<FillMonth> fillMonth() {
		List<FillMonth> al = new ArrayList<FillMonth>();

		try {

			al.add(new FillMonth("1", "January"));
			al.add(new FillMonth("2", "February"));
			al.add(new FillMonth("3", "March"));
			al.add(new FillMonth("4", "April"));
			al.add(new FillMonth("5", "May"));
			al.add(new FillMonth("6", "June"));
			al.add(new FillMonth("7", "July"));
			al.add(new FillMonth("8", "August"));
			al.add(new FillMonth("9", "September"));
			al.add(new FillMonth("10", "October"));
			al.add(new FillMonth("11", "November"));
			al.add(new FillMonth("12", "December"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	public List<FillMonth> fillSixMonth() {
		List<FillMonth> al = new ArrayList<FillMonth>();
		try {

			al.add(new FillMonth("4,5,6,7,8,9", "April,May,June,July,August,September"));
			al.add(new FillMonth("10,11,12,1,2,3", "October,November,December,January,February,March"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	public List<FillMonth> fillHalfYears() {
		List<FillMonth> al = new ArrayList<FillMonth>();
		try {

			al.add(new FillMonth("1", "First Half"));
			al.add(new FillMonth("2", "Second Half"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	public List<FillMonth> fillQuarters() {
		List<FillMonth> al = new ArrayList<FillMonth>();
		try {

			al.add(new FillMonth("1", "Quarter 1"));
			al.add(new FillMonth("2", "Quarter 2"));
			al.add(new FillMonth("3", "Quarter 3"));
			al.add(new FillMonth("4", "Quarter 4"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	

	public List<FillMonth> fillQuarterlyMonth() {
		List<FillMonth> al = new ArrayList<FillMonth>();
		try {

			al.add(new FillMonth("1,2,3", "Q4 (January,February,March)"));
			al.add(new FillMonth("4,5,6", "Q1 (April,May,June)"));
			al.add(new FillMonth("7,8,9", "Q2 (July,August,September)"));
			al.add(new FillMonth("10,11,12", "Q3 (October,November,December)"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public List<FillMonth> fillQuarterlyMonthNew() {
		List<FillMonth> al = new ArrayList<FillMonth>();
		try {

			al.add(new FillMonth("1,2,3", "Q1 (January,February,March)"));
			al.add(new FillMonth("4,5,6", "Q2 (April,May,June)"));
			al.add(new FillMonth("7,8,9", "Q3 (July,August,September)"));
			al.add(new FillMonth("10,11,12", "Q4 (October,November,December)"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getMonthId() {
		return monthId;
	}

	public void setMonthId(String monthId) {
		this.monthId = monthId;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

}
