package com.konnect.jpms.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.IStatements;

public class FillAlignedType implements IStatements{

	String alignTypeName;
	String alignTypeId;
	
	public FillAlignedType(String alignTypeId, String alignTypeName) {
		this.alignTypeId = alignTypeId;
		this.alignTypeName = alignTypeName;
	}
	
	HttpServletRequest request;
	public FillAlignedType(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillAlignedType() {
		
	}
	
	public List<FillAlignedType> fillAlignedTypeList(String pageFrom) {
		List<FillAlignedType> al = new ArrayList<FillAlignedType>();
		if(pageFrom == null || pageFrom.trim().equals("") || pageFrom.trim().equals("null")) {
			al.add(new FillAlignedType(PROJECT+"", "Poject"));
		}
			al.add(new FillAlignedType(TASK+"", "Task"));
		if(pageFrom == null || pageFrom.trim().equals("") || pageFrom.trim().equals("null")) {
			al.add(new FillAlignedType(PRO_TIMESHEET+"", "Project Timesheet"));
//			al.add(new FillAlignedType(DOCUMENT+"", "Document"));
			al.add(new FillAlignedType(INVOICE+"", "Invoice"));
		}
		return al;
	}
	
	
	public String getAlignTypeName() {
		return alignTypeName;
	}

	public void setAlignTypeName(String alignTypeName) {
		this.alignTypeName = alignTypeName;
	}

	public String getAlignTypeId() {
		return alignTypeId;
	}

	public void setAlignTypeId(String alignTypeId) {
		this.alignTypeId = alignTypeId;
	}

}
