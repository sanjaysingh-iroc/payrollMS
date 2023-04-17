package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class FillTimeFormats implements IStatements {

	String timeFormatId;
	String timeFormatName;

	private FillTimeFormats(String timeFormatId, String timeFormatName) {
		this.timeFormatId = timeFormatId;
		this.timeFormatName = timeFormatName;

	}

	public FillTimeFormats() {
	}

	public List<FillTimeFormats> fillTimeFormats() {

		List<FillTimeFormats> al = new ArrayList<FillTimeFormats>();
		try {

			al.add(new FillTimeFormats("HH:mm", "15:00"));
			al.add(new FillTimeFormats("hh:mma", "3:00AM"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getTimeFormatId() {
		return timeFormatId;
	}

	public void setTimeFormatId(String timeFormatId) {
		this.timeFormatId = timeFormatId;
	}

	public String getTimeFormatName() {
		return timeFormatName;
	}

	public void setTimeFormatName(String timeFormatName) {
		this.timeFormatName = timeFormatName;
	}

}
