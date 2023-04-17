package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.IStatements;

public class FillAllowancePaymentLogic implements IStatements {
	String paymentLogicId;
	String paymentLogicName;
	
	public String getPaymentLogicId() {
		return paymentLogicId;
	}
	public void setPaymentLogicId(String paymentLogicId) {
		this.paymentLogicId = paymentLogicId;
	}
	public String getPaymentLogicName() {
		return paymentLogicName;
	}
	public void setPaymentLogicName(String paymentLogicName) {
		this.paymentLogicName = paymentLogicName;
	}
	public FillAllowancePaymentLogic() {
		super();
	}
	public FillAllowancePaymentLogic(String paymentLogicId, String paymentLogicName) {
		super();
		this.paymentLogicId = paymentLogicId;
		this.paymentLogicName = paymentLogicName;
	}
	
	public List<FillAllowancePaymentLogic> fillAllowancePaymentLogic() {
		List<FillAllowancePaymentLogic> al = new ArrayList<FillAllowancePaymentLogic>();
		try{
			al.add(new FillAllowancePaymentLogic(""+A_FIXED_ONLY_ID, A_FIXED_ONLY));
			al.add(new FillAllowancePaymentLogic(""+A_FIXED_X_DAYS_ID, A_FIXED_X_DAYS));
			al.add(new FillAllowancePaymentLogic(""+A_FIXED_X_HOURS_ID, A_FIXED_X_HOURS));
			al.add(new FillAllowancePaymentLogic(""+A_FIXED_X_CUSTOM_ID, A_FIXED_X_CUSTOM));
			al.add(new FillAllowancePaymentLogic(""+A_EQUAL_TO_SALARY_HEAD_ID, A_EQUAL_TO_SALARY_HEAD));
			al.add(new FillAllowancePaymentLogic(""+A_SALARY_HEAD_X_DAYS_ID, A_SALARY_HEAD_X_DAYS));
			al.add(new FillAllowancePaymentLogic(""+A_SALARY_HEAD_X_HOURS_ID, A_SALARY_HEAD_X_HOURS));
			al.add(new FillAllowancePaymentLogic(""+A_SALARY_HEAD_X_CUSTOM_ID, A_SALARY_HEAD_X_CUSTOM));
			al.add(new FillAllowancePaymentLogic(""+A_FIXED_X_ACHIEVED_ID, A_FIXED_X_ACHIEVED));
			al.add(new FillAllowancePaymentLogic(""+A_SALARY_HEAD_X_ACHIEVED_ID, A_SALARY_HEAD_X_ACHIEVED));
			al.add(new FillAllowancePaymentLogic(""+A_FIXED_AND_PER_HOUR_ID, A_FIXED_AND_PER_HOUR));
			al.add(new FillAllowancePaymentLogic(""+A_FIXED_AND_PER_DAY_ID, A_FIXED_AND_PER_DAY));
			al.add(new FillAllowancePaymentLogic(""+A_FIXED_ONLY_DEDUCTION_ID, A_FIXED_ONLY_DEDUCTION));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	public List<FillAllowancePaymentLogic> fillAllowancePaymentLogic(int nAllowanceCondition) {
		List<FillAllowancePaymentLogic> al = new ArrayList<FillAllowancePaymentLogic>();
		try{
			if(nAllowanceCondition == A_NO_OF_DAYS_ID) {
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_ONLY_ID, A_FIXED_ONLY));
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_AND_PER_DAY_ID, A_FIXED_AND_PER_DAY));
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_X_DAYS_ID, A_FIXED_X_DAYS));
				al.add(new FillAllowancePaymentLogic(""+A_EQUAL_TO_SALARY_HEAD_ID, A_EQUAL_TO_SALARY_HEAD));
				al.add(new FillAllowancePaymentLogic(""+A_SALARY_HEAD_X_DAYS_ID, A_SALARY_HEAD_X_DAYS));
			} else if(nAllowanceCondition == A_NO_OF_HOURS_ID) {
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_ONLY_ID, A_FIXED_ONLY));
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_AND_PER_HOUR_ID, A_FIXED_AND_PER_HOUR));
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_X_HOURS_ID, A_FIXED_X_HOURS));
				al.add(new FillAllowancePaymentLogic(""+A_EQUAL_TO_SALARY_HEAD_ID, A_EQUAL_TO_SALARY_HEAD));
				al.add(new FillAllowancePaymentLogic(""+A_SALARY_HEAD_X_HOURS_ID, A_SALARY_HEAD_X_HOURS));
			} else if(nAllowanceCondition == A_CUSTOM_FACTOR_ID) {
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_ONLY_ID, A_FIXED_ONLY));
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_X_CUSTOM_ID, A_FIXED_X_CUSTOM));
				al.add(new FillAllowancePaymentLogic(""+A_EQUAL_TO_SALARY_HEAD_ID, A_EQUAL_TO_SALARY_HEAD));
				al.add(new FillAllowancePaymentLogic(""+A_SALARY_HEAD_X_CUSTOM_ID, A_SALARY_HEAD_X_CUSTOM));
			} else if(nAllowanceCondition == A_GOAL_KRA_TARGET_ID || nAllowanceCondition == A_KRA_ID) {
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_ONLY_ID, A_FIXED_ONLY));
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_X_ACHIEVED_ID, A_FIXED_X_ACHIEVED));
				al.add(new FillAllowancePaymentLogic(""+A_EQUAL_TO_SALARY_HEAD_ID, A_EQUAL_TO_SALARY_HEAD));
				al.add(new FillAllowancePaymentLogic(""+A_SALARY_HEAD_X_ACHIEVED_ID, A_SALARY_HEAD_X_ACHIEVED));
			} else if(nAllowanceCondition == A_NO_OF_DAYS_ABSENT_ID) {
				al.add(new FillAllowancePaymentLogic(""+A_FIXED_ONLY_DEDUCTION_ID, A_FIXED_ONLY_DEDUCTION));
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	

}
