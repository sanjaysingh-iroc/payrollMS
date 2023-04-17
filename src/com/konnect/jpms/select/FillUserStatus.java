package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillUserStatus {

	String statusId;
	String statusName;
	
	public FillUserStatus(){}
	
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
	
	public FillUserStatus(String statusId, String statusName) {
		this.statusId = statusId;
		this.statusName = statusName;
	}
	
	public List<FillUserStatus> fillUserStatus(){
		List<FillUserStatus> al = new ArrayList<FillUserStatus>();
		al.add(new FillUserStatus("ACTIVE","Active"));
		al.add(new FillUserStatus("SUSPENDED","Suspended"));
		al.add(new FillUserStatus("TERMINATED","Terminated"));
		
		return al;
	}	
}
