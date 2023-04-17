package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillPerkPaymentCycle{

	String perkPaymentCycleId;
	String perkPaymentCycleName;
	
	private FillPerkPaymentCycle(String perkPaymentCycleId, String perkPaymentCycleName) {
		this.perkPaymentCycleId = perkPaymentCycleId;
		this.perkPaymentCycleName = perkPaymentCycleName;
	}
	
	public FillPerkPaymentCycle() {
	}
	
	public List<FillPerkPaymentCycle> fillPerkPaymentCycle(){
		
		List<FillPerkPaymentCycle> al = new ArrayList<FillPerkPaymentCycle>();

		try {

			al.add(new FillPerkPaymentCycle("A", "Annual"));
			al.add(new FillPerkPaymentCycle("M", "Monthly"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getPerkPaymentCycleId() {
		return perkPaymentCycleId;
	}

	public void setPerkPaymentCycleId(String perkPaymentCycleId) {
		this.perkPaymentCycleId = perkPaymentCycleId;
	}

	public String getPerkPaymentCycleName() {
		return perkPaymentCycleName;
	}

	public void setPerkPaymentCycleName(String perkPaymentCycleName) {
		this.perkPaymentCycleName = perkPaymentCycleName;
	}



}  
