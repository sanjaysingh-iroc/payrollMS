package com.konnect.jpms.util;

import java.util.Comparator;

public class ComparatorWeight implements Comparable<ComparatorWeight>,Comparator<ComparatorWeight>{
	
	String strName;
	int weight;
	Boolean variable;
	
	public ComparatorWeight(){}
	public ComparatorWeight(String strName, int weight,Boolean variable){
		this.strName = strName;
		this.weight = weight;
		this.variable=variable;
	}
	public String getStrName() {
		return strName;
	}
	public void setStrName(String strName) {
		this.strName = strName;
	}
	public Boolean isVariable() {
		return variable;
	}
	public void setVariable(Boolean variable) {
		this.variable = variable;
	}
	
	@Override
	public String toString() {
		return "ComparatorWeight [strName=" + strName + ", weight=" + weight
				+ ", variable=" + variable + "]";
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	
	@Override
	public int compareTo(ComparatorWeight o) {
		int compare=this.variable.compareTo(o.variable);
		if(compare==0){
			return this.weight-o.weight;
		}else
			return compare;
	}
	@Override
	public int compare(ComparatorWeight arg0, ComparatorWeight arg1) {
		return arg0.variable.compareTo(arg1.variable);
	}
	
}