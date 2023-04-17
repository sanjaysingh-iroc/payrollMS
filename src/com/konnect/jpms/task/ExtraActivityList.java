package com.konnect.jpms.task;

import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.IStatements;

public class ExtraActivityList implements IStatements {

	String ExtraActivityID;
	String ExtraActivityName;

	public ExtraActivityList() {
	}
 
	public ExtraActivityList(String ExtraActivityID, String ExtraActivityName) {
		this.ExtraActivityID = ExtraActivityID;
		this.ExtraActivityName = ExtraActivityName;
	}

	public List<ExtraActivityList> fillExtraActivity() {

		List<ExtraActivityList> al = new ArrayList<ExtraActivityList>();

		al.add(new ExtraActivityList("a Desk Job", "a Desk Job"));
		al.add(new ExtraActivityList("a Call", "a Call"));
		al.add(new ExtraActivityList("a Sales Call", "a Sales Call"));
		al.add(new ExtraActivityList("a Conference Call", "a Conference Call"));
		al.add(new ExtraActivityList("a Meeting with my Supervisor",
				"a Meeting with my Supervisor"));
		al.add(new ExtraActivityList("a Meeting with my Subordinate",
				"a Meeting with my Subordinate"));
		al.add(new ExtraActivityList("a Meeting with HR", "a Meeting with HR"));
		al.add(new ExtraActivityList("a Team Meeting", "a Team Meeting"));
		al.add(new ExtraActivityList("a Meeting with Client",
				"a Meeting with Client"));
		al.add(new ExtraActivityList("a Client Demo", "a Client Demo"));
		al.add(new ExtraActivityList("a Client Visit", "a Client Visit"));
		al.add(new ExtraActivityList("a Field Visit", "a Field Visit"));
		al.add(new ExtraActivityList("a Tranning Session", "a Tranning Session"));
		al.add(new ExtraActivityList("to Pantry", "to Pantry"));
		al.add(new ExtraActivityList("a Coffee Break", "a Coffee Break"));
		al.add(new ExtraActivityList("a Break", "a Break"));

		return al;
	}

	public String getExtraActivityID() {
		return ExtraActivityID;
	}

	public void setExtraActivityID(String extraActivityID) {
		ExtraActivityID = extraActivityID;
	}

	public String getExtraActivityName() {
		return ExtraActivityName;
	}

	public void setExtraActivityName(String extraActivityName) {
		ExtraActivityName = extraActivityName;
	}

}