package com.konnect.jpms.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LoadSelect {
	  
	private List<String> Parse(String str) {
	    List<String> output = new ArrayList<String>();
	    Matcher match = Pattern.compile("[0-9]+|[a-z]+|[A-Z]+").matcher(str);
	    while (match.find()) {
	        output.add(match.group());
	    }
	    return output;
	}
	
	static public void  main(String args[]){
		
//		double x = 4.0666; 
//		LoadSelect ls = new LoadSelect();
//		System.out.println("list ==>>>>> " + ls.Parse("ASDDAA12345awe"));
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = new CommonFunctions();
		String duration = uF.getTimeDurationWithNoSpan("2019-08-19", CF, uF, null);
		System.out.println("duration ==>"+duration);
//		System.out.println("ceil==>"+Math.ceil(x));
//		System.out.println("floor==>"+Math.floor(x));
//		System.out.println("round==>"+Math.round(x));
		
		
	}
}


