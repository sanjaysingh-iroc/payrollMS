package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.UtilityFunctions;

public class FillAttendanceFilter {

	String attendFilterId;
	String attendFilterName;
	UtilityFunctions uF=new UtilityFunctions();
	private FillAttendanceFilter(String attendFilterId, String attendFilterName) {
		this.attendFilterId = attendFilterId;
		this.attendFilterName = attendFilterName;
	}
	
	public FillAttendanceFilter() {
	}
	 
	public List<FillAttendanceFilter> fillAttendanceFilter(){
		List<FillAttendanceFilter> al = new ArrayList<FillAttendanceFilter>();
		
		try {
			al.add(new FillAttendanceFilter("A", "All"));
			al.add(new FillAttendanceFilter("CL", "Came Late"));
			al.add(new FillAttendanceFilter("CE", "Came Early"));
			al.add(new FillAttendanceFilter("COT", "Came On Time"));
			
			al.add(new FillAttendanceFilter("LL", "Left Late"));
			al.add(new FillAttendanceFilter("LE", "Left Early"));
			al.add(new FillAttendanceFilter("LOT", "Left On Time"));
			
			al.add(new FillAttendanceFilter("AB", "Absent"));
			al.add(new FillAttendanceFilter("L", "On Leave"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getAttendFilterId() {
		return attendFilterId;
	}

	public void setAttendFilterId(String attendFilterId) {
		this.attendFilterId = attendFilterId;
	}

	public String getAttendFilterName() {
		return attendFilterName;
	}

	public void setAttendFilterName(String attendFilterName) {
		this.attendFilterName = attendFilterName;
	}
	
}  
