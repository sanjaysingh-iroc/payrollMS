package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillApproval {

	String approvalId;
	String approvalName;

	public String getApprovalId() {
		return approvalId;
	}

	public void setApprovalId(String approvalId) {
		this.approvalId = approvalId;
	}

	public String getApprovalName() {
		return approvalName;
	}

	public void setApprovalName(String approvalName) {
		this.approvalName = approvalName;
	}

	public FillApproval(String approvalId, String approvalName) {
		this.approvalId = approvalId;
		this.approvalName = approvalName;
	}

	public FillApproval() {
	}

	public List<FillApproval> fillYesNo() {
		List<FillApproval> al = new ArrayList<FillApproval>();

		try {
			al.add(new FillApproval("YES", "Yes"));
			al.add(new FillApproval("NO", "No"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public List<FillApproval> fillTrueFlase() {
		List<FillApproval> al = new ArrayList<FillApproval>();

		try {
			al.add(new FillApproval("TRUE", "True"));
			al.add(new FillApproval("FALSE", "False"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public List<FillApproval> fillApprovalDenied() {
		List<FillApproval> al = new ArrayList<FillApproval>();

		try {
			al.add(new FillApproval("1", "Approve"));
			al.add(new FillApproval("-1", "Deny"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	public List<FillApproval> fillLeaveStartDate() {
		List<FillApproval> al = new ArrayList<FillApproval>();

		try {
//			al.add(new FillApproval("JD", "Joining date"));
			al.add(new FillApproval("CY", "Calendar Year"));
			al.add(new FillApproval("FY", "Financial Year"));
			al.add(new FillApproval("CMY", "Calendar Mid-Year"));
			al.add(new FillApproval("FMY", "Financial Mid-Year"));
//			al.add(new FillApproval("CD", "ChooseDate"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	public String getBoolValue(String str) {
		
		if (str != null && (str.equalsIgnoreCase("TRUE") || str.equalsIgnoreCase("T"))) {
			return "YES";
		} else {
			return "NO";
		}

	}

}
