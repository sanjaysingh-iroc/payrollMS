package com.konnect.jpms.test;

import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.List;

public class ListingAction extends ActionSupport {
	private List lstList1 = null;

	public String execute() throws Exception {
		populateDetail();
		return SUCCESS;
	}

	private void populateDetail() {
		lstList1 = new ArrayList();
		lstList1.add("Fruits");
		lstList1.add("Places");
		lstList1.add("Others");

	}

	public List getLstList1() {
		return lstList1;
	}

	public void setLstList1(List lstList1) {
		this.lstList1 = lstList1;
	}
}