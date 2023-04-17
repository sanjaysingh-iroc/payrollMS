package com.konnect.jpms.performance;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class MyDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SortedSet<String> treeSet = new TreeSet<String>();
		treeSet.add("1");
		treeSet.add("myreview1");
		treeSet.add("myreview");
		treeSet.add("2");
		treeSet.add("myreview3");
		treeSet.add("myreview4");
		treeSet.add("5");
		treeSet.add("myreview5");
		treeSet.add("myreview6");
		
		Iterator<String> it = treeSet.iterator();
		while(it.hasNext()){
			System.out.println("for \t"+it.next());
		}

	}

}
