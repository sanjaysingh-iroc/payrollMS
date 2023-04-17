package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.IStatements;

public class FillWeekDays implements IStatements{
 
	
	String weekDayId;
	String weekDayName;
	
	private FillWeekDays(String weekDayId, String weekDayName) {
		this.weekDayId = weekDayId;
		this.weekDayName = weekDayName;
	}
	
	public FillWeekDays() {
	}
	
	public List<FillWeekDays> fillWeekDays(){
		
		List<FillWeekDays> al = new ArrayList<FillWeekDays>();
		
		al.add(new FillWeekDays(SUNDAY, SUNDAY));
		al.add(new FillWeekDays(MONDAY, MONDAY));
		al.add(new FillWeekDays(TUESDAY, TUESDAY));
		al.add(new FillWeekDays(WEDNESDAY, WEDNESDAY));
		al.add(new FillWeekDays(THURSDAY, THURSDAY));
		al.add(new FillWeekDays(FRIDAY, FRIDAY));
		al.add(new FillWeekDays(SATURDAY, SATURDAY));
		
		
		return al;
	}
	
public List<FillWeekDays> fillWeekDays(String wOffDay1, String wOffDay2){
		
		List<FillWeekDays> al = new ArrayList<FillWeekDays>();
		if(wOffDay1 != null && !wOffDay1.equals(SUNDAY) && wOffDay2 != null && !wOffDay2.equals(SUNDAY)) { 
			al.add(new FillWeekDays(SUNDAY, SUNDAY));
		}
		if(wOffDay1 != null && !wOffDay1.equals(MONDAY) && wOffDay2 != null && !wOffDay2.equals(MONDAY)) { 
			al.add(new FillWeekDays(MONDAY, MONDAY));
		}
		if(wOffDay1 != null && !wOffDay1.equals(TUESDAY) && wOffDay2 != null && !wOffDay2.equals(TUESDAY)) { 
			al.add(new FillWeekDays(TUESDAY, TUESDAY));
		}
		if(wOffDay1 != null && !wOffDay1.equals(WEDNESDAY) && wOffDay2 != null && !wOffDay2.equals(WEDNESDAY)) { 
			al.add(new FillWeekDays(WEDNESDAY, WEDNESDAY));
		}
		if(wOffDay1 != null && !wOffDay1.equals(THURSDAY) && wOffDay2 != null && !wOffDay2.equals(THURSDAY)) { 
			al.add(new FillWeekDays(THURSDAY, THURSDAY));
		}
		if(wOffDay1 != null && !wOffDay1.equals(FRIDAY) && wOffDay2 != null && !wOffDay2.equals(FRIDAY)) { 
			al.add(new FillWeekDays(FRIDAY, FRIDAY));
		}
		if(wOffDay1 != null && !wOffDay1.equals(SATURDAY) && wOffDay2 != null && !wOffDay2.equals(SATURDAY)) { 
			al.add(new FillWeekDays(SATURDAY, SATURDAY));
		}
		
		return al;
	}
	
	public List<FillWeekDays> fillWeekNos(){
		
		List<FillWeekDays> al = new ArrayList<FillWeekDays>();
		
		al.add(new FillWeekDays("1", "1st"));
		al.add(new FillWeekDays("2", "2nd"));
		al.add(new FillWeekDays("3", "3rd"));
		al.add(new FillWeekDays("4", "4th"));
		al.add(new FillWeekDays("5", "5th"));
//		al.add(new FillWeekDays("6", "6th"));
		return al;
	}

	public List<FillWeekDays> fillWeeklyOffType(){
		List<FillWeekDays> al = new ArrayList<FillWeekDays>();
		
		al.add(new FillWeekDays("FD", "Full Day"));
		al.add(new FillWeekDays("HD", "Half Day"));
//		al.add(new FillWeekDays("AD", "Alternate Day"));
		
		return al;
	}
	
	public String getWeekDayId() {
		return weekDayId;
	}

	public void setWeekDayId(String weekDayId) {
		this.weekDayId = weekDayId;
	}

	public String getWeekDayName() {
		return weekDayName;
	}

	public void setWeekDayName(String weekDayName) {
		this.weekDayName = weekDayName;
	}

	public List<FillWeekDays> fillWeekDays(int size) {
List<FillWeekDays> al = new ArrayList<FillWeekDays>();
		
		al.add(new FillWeekDays("1", "1st"));
		al.add(new FillWeekDays("2", "2nd"));
		al.add(new FillWeekDays("3", "3rd"));
		al.add(new FillWeekDays("4", "4th"));
		if(size == 5){
			al.add(new FillWeekDays("5", "5th"));
		} else if(size == 6){
			al.add(new FillWeekDays("5", "5th"));
			al.add(new FillWeekDays("6", "6th"));
		}
		return al;
	}
	
}  
