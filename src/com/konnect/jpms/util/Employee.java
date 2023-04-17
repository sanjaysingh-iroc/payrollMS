package com.konnect.jpms.util;



public class Employee {
	
	String strName;
	String strEmpId;

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
		
	}
	
	public Employee(String strName, String strEmpId) {
	    super();
	    this.strName = strName;
	    this.strEmpId = strEmpId;
	  }
	
	

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

}
