package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class FillStatus {

	String statusId;
	String statusName;
	
	public FillStatus(String  statusId, String statusName) {
		this.statusId = statusId;
		this.statusName = statusName;
	}
	
	HttpServletRequest request;
	public FillStatus(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillStatus() {
		
	}
	
	public List<FillStatus> fillStatus(){
		
		List<FillStatus> al = new ArrayList<FillStatus>();
		al.add(new FillStatus("1","Approved"));
		al.add(new FillStatus("0","Pending"));
		al.add(new FillStatus("-1","Rejected"));
		
		return al;
	}

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
}

	

