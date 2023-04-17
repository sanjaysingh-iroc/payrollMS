package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.IConstants;

public class FillEmployeeStatus implements IConstants{

	String statusId;
	String statusName;
	
	public FillEmployeeStatus(){}
	
	public String getStatusId() {
		return statusId;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public String getStatusName() { 
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
	public FillEmployeeStatus(String statusId, String statusName) {
		this.statusId = statusId;
		this.statusName = statusName;
	}
	
	public List<FillEmployeeStatus> fillEmployeeStatus(){
		List<FillEmployeeStatus> al = new ArrayList<FillEmployeeStatus>();
		al.add(new FillEmployeeStatus(PROBATION, "Probation"));
		al.add(new FillEmployeeStatus(PERMANENT, "Permanent"));
		al.add(new FillEmployeeStatus(RESIGNED, "Resigned"));
//		al.add(new FillEmployeeStatus(TEMPORARY, "Temporary"));
		al.add(new FillEmployeeStatus(TERMINATED,"Terminated"));
		return al;
	}	
	
	
	public List<FillEmployeeStatus> fillEmployeeLiveStatus(HttpServletRequest request){
		List<FillEmployeeStatus> al = new ArrayList<FillEmployeeStatus>();
		al.add(new FillEmployeeStatus(PROBATION, "Probation"));
		al.add(new FillEmployeeStatus(PERMANENT, "Permanent"));
		al.add(new FillEmployeeStatus(TEMPORARY, "Temporary"));
		return al;
	}
}
