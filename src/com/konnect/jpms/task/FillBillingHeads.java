package com.konnect.jpms.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.IStatements;

public class FillBillingHeads implements IStatements{

	String headName;
	String headId;
	
	
	
	public FillBillingHeads(String headId, String headName) {
		this.headId = headId;
		this.headName = headName;
	}
	
	HttpServletRequest request;
	public FillBillingHeads(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillBillingHeads() {
	}
	
	public List<FillBillingHeads> fillTAxDeductionTypeList(){
		List<FillBillingHeads> al = new ArrayList<FillBillingHeads>();
		al.add(new FillBillingHeads(TD_INVOICE+"", 				"Invoice"));
		al.add(new FillBillingHeads(TD_CUSTOMER+"", 			"Customer Deduction"));
//		al.add(new FillBillingHeads(TD_BOTH+"", 				"Both"));
		return al;
	}
	
	
	public List<FillBillingHeads> fillBillingHeadDataTypeList(){
		List<FillBillingHeads> al = new ArrayList<FillBillingHeads>();
		al.add(new FillBillingHeads(DT_FIXED+"", 				"Fixed"));
		al.add(new FillBillingHeads(DT_PRORATA_INDIVIDUAL+"", 	"Pro-rata(Individual)"));
		al.add(new FillBillingHeads(DT_PRORATA_OVERALL+"", 		"Pro-rata(Cumulative)"));
		al.add(new FillBillingHeads(DT_OPE_INDIVIDUAL+"", 		"OPE(Individual)"));
		al.add(new FillBillingHeads(DT_OPE_OVERALL+"", 			"OPE(Cumulative)"));
		al.add(new FillBillingHeads(DT_MILESTONE+"", 			"Milestone"));
		al.add(new FillBillingHeads(DT_OPE+"", 					"OPE"));
		return al;
	}
	
	
	public List<FillBillingHeads> fillBillingHeadDataTypeListBillingTypewise(String billingType) {
		List<FillBillingHeads> al = new ArrayList<FillBillingHeads>();
		al.add(new FillBillingHeads(DT_FIXED+"", 				"Fixed"));
		if(billingType != null && billingType.equals("F")) {
			al.add(new FillBillingHeads(DT_MILESTONE+"", 			"Milestone"));
			al.add(new FillBillingHeads(DT_OPE+"", 					"OPE"));
		} else {
			al.add(new FillBillingHeads(DT_PRORATA_INDIVIDUAL+"", 	"Pro-rata(Individual)"));
			al.add(new FillBillingHeads(DT_PRORATA_OVERALL+"", 		"Pro-rata(Cumulative)"));
			al.add(new FillBillingHeads(DT_OPE_INDIVIDUAL+"", 		"OPE(Individual)"));
			al.add(new FillBillingHeads(DT_OPE_OVERALL+"", 			"OPE(Cumulative)"));
		}
		return al;
	}
	
	
	public List<FillBillingHeads> fillBillingHeadOtherVariableList() {
		List<FillBillingHeads> al = new ArrayList<FillBillingHeads>();
		al.add(new FillBillingHeads(OV_ONLY_RESOURCE+"", 	"Only Resouce"));
		al.add(new FillBillingHeads(OV_ONLY_TASK+"", 		"Only Task"));
		al.add(new FillBillingHeads(OV_BOTH+"", 			"Both"));
		return al;
	}
	
	
	public List<FillBillingHeads> fillBillingHeadOtherVariableListBillingTypewise(String billingHeadDataType) {
		List<FillBillingHeads> al = new ArrayList<FillBillingHeads>();
		if(billingHeadDataType != null && billingHeadDataType.equals(DT_OPE_INDIVIDUAL+"")) {
			al.add(new FillBillingHeads(OV_ONLY_RESOURCE+"", 	"Only Resouce"));
		} else {
			al.add(new FillBillingHeads(OV_ONLY_RESOURCE+"", 	"Only Resouce"));
			al.add(new FillBillingHeads(OV_ONLY_TASK+"", 		"Only Task"));
			al.add(new FillBillingHeads(OV_BOTH+"", 			"Both"));
		}
		return al;
	}

	public String getHeadName() {
		return headName;
	}

	public void setHeadName(String headName) {
		this.headName = headName;
	}

	public String getHeadId() {
		return headId;
	}

	public void setHeadId(String headId) {
		this.headId = headId;
	}

	
}
