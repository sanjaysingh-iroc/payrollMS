package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GetPriorityList {

	String priId;
	String proName;

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getPriId() {
		return priId;
	}

	public void setPriId(String priId) {
		this.priId = priId;
	}

	public GetPriorityList(String priId, String proName) {
		this.priId = priId;
		this.proName = proName;
	}

	public GetPriorityList() {
	}

	public List<GetPriorityList> fillPriorityList() {
		ArrayList<GetPriorityList> priorityList = new ArrayList<GetPriorityList>();
		// UtilityFunctions uF = new UtilityFunctions();
		priorityList.add(new GetPriorityList("0", "Low"));
		priorityList.add(new GetPriorityList("1", "Medium"));
		priorityList.add(new GetPriorityList("2", "High"));
		return priorityList;
	}
	
	public String getPriority(int nPriority) {
		
		String strPriority="";
		switch(nPriority){
		case 0:
			strPriority = "Low";
			break;
		case 1:
			strPriority = "Medium";
			break;
		case 2:
			strPriority = "High";
			break;
		}
		return strPriority;
				
		
	}
	
}
