package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillSalaryCalculationTypes {

	String salaryCalcId;
	String salaryCalcName;

	

	public FillSalaryCalculationTypes(String salaryCalcId, String salaryCalcName) {
		this.salaryCalcId = salaryCalcId;
		this.salaryCalcName = salaryCalcName;
	}

	public FillSalaryCalculationTypes() {
	}

	public List<FillSalaryCalculationTypes> fillSalaryCalculationTypes() {
		List<FillSalaryCalculationTypes> al = new ArrayList<FillSalaryCalculationTypes>();

		try {
			al.add(new FillSalaryCalculationTypes("AMD", "Actual Month Days"));
			al.add(new FillSalaryCalculationTypes("AWD", "Actual Working Days"));
			al.add(new FillSalaryCalculationTypes("AFD", "Fixed Days"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getSalaryCalcId() {
		return salaryCalcId;
	}

	public void setSalaryCalcId(String salaryCalcId) {
		this.salaryCalcId = salaryCalcId;
	}

	public String getSalaryCalcName() {
		return salaryCalcName;
	}

	public void setSalaryCalcName(String salaryCalcName) {
		this.salaryCalcName = salaryCalcName;
	}

}
