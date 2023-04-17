package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillInOut {

	String in_out_Id;
	String in_out_Name;

	public String getIn_out_Id() {
		return in_out_Id;
	}

	public void setIn_out_Id(String in_out_Id) {
		this.in_out_Id = in_out_Id;
	}

	public String getIn_out_Name() {
		return in_out_Name;
	}

	public void setIn_out_Name(String in_out_Name) {
		this.in_out_Name = in_out_Name;
	}

	public FillInOut(String in_out_Id, String in_out_Name) {
		this.in_out_Id = in_out_Id;
		this.in_out_Name = in_out_Name;
	}

	public FillInOut() {
	}

	public List<FillInOut> fillInOut() {
		List<FillInOut> al = new ArrayList<FillInOut>();

		try {

			al.add(new FillInOut("IN", "IN"));
			al.add(new FillInOut("OUT", "OUT"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
 
	public List<FillInOut> fillInOutAll() {
		List<FillInOut> al = new ArrayList<FillInOut>();

		try {
			al.add(new FillInOut("A", "ALL"));
			al.add(new FillInOut("IN", "IN"));
			al.add(new FillInOut("OUT", "OUT"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

}
