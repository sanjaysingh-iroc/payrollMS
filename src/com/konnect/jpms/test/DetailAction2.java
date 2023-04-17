package com.konnect.jpms.test;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

public class DetailAction2 extends ActionSupport {
	private String lst;
	private String country;
	
	private List lstList = null;
	private List lstList2 = null;

	public String execute() throws Exception {

		System.out.println("getCountry()=====>"+getCountry());
		
		  
//		if (getLst() != null && !getLst().equals("")) {
		if (getCountry() != null && !getCountry().equals("")) {
			populateDetail(getCountry());
			return SUCCESS;
		} else {
			lstList = new ArrayList();
			lstList.add("Apple");
			lstList.add("PineApple");
			lstList.add("Mango");
			lstList.add("Banana");
			lstList.add("Grapes");
			
			return SUCCESS;
		}
	}
	
	

	private void populateDetail(String id) {
		 
		System.out.println("id=====>"+id);
		
		lstList = new ArrayList();
		if (id.equalsIgnoreCase("Fruits")) {
			lstList.add("Apple");
			lstList.add("PineApple");
			lstList.add("Mango");
			lstList.add("Banana");
			lstList.add("Grapes");
		} else if (id.equalsIgnoreCase("Places")) {
			lstList.add("New York");
			lstList.add("Sydney");
			lstList.add("California");
			lstList.add("Switzerland");
			lstList.add("Paris");
		}else if (id.equalsIgnoreCase("India 12")) {
			lstList.add("India 12");
			lstList.add("Sydney");
			lstList.add("California");
			lstList.add("Switzerland");
			lstList.add("Paris");
		}else if (id.equalsIgnoreCase("1")) {
			lstList.add("India");
			lstList.add("Sydney");
			lstList.add("California");
			lstList.add("Switzerland");
			lstList.add("Paris");
		} else {
			lstList.add("Other 1");
			lstList.add("Other 2");
			lstList.add("Other 3");
			lstList.add("Other 4");
			lstList.add("Other 5");
		}
	}

	public List getLstList() {
		return lstList;
	}

	public void setLstList(List lstList) {
		this.lstList = lstList;
	}

	public String getLst() {
		return lst;
	}

	public void setLst(String lst) {
		this.lst = lst;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}