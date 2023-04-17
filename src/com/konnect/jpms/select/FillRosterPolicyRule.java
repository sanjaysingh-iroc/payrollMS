package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.IStatements;

public class FillRosterPolicyRule implements IStatements{
 
	
	String rosterPolicyRuleId;
	String rosterPolicyRuleName;
	
	private FillRosterPolicyRule(String rosterPolicyRuleId, String rosterPolicyRuleName) {
		this.rosterPolicyRuleId = rosterPolicyRuleId;
		this.rosterPolicyRuleName = rosterPolicyRuleName;
	}
	
	public FillRosterPolicyRule() {}
	
	public List<FillRosterPolicyRule> fillRosterPolicyRule(){
		
		List<FillRosterPolicyRule> al = new ArrayList<FillRosterPolicyRule>();
		
		al.add(new FillRosterPolicyRule("1", "Shift"));
		al.add(new FillRosterPolicyRule("2", "Employee"));
		al.add(new FillRosterPolicyRule("3", "Gender"));
		al.add(new FillRosterPolicyRule("4", "Combine Shift"));
		
		return al;
	}

	public String getRosterPolicyRuleId() {
		return rosterPolicyRuleId;
	}

	public void setRosterPolicyRuleId(String rosterPolicyRuleId) {
		this.rosterPolicyRuleId = rosterPolicyRuleId;
	}

	public String getRosterPolicyRuleName() {
		return rosterPolicyRuleName;
	}

	public void setRosterPolicyRuleName(String rosterPolicyRuleName) {
		this.rosterPolicyRuleName = rosterPolicyRuleName;
	}
	
	
}  
