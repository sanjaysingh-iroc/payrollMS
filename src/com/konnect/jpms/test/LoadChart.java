package com.konnect.jpms.test;

import com.konnect.jpms.util.EncryptionUtils;

public class LoadChart {

	public static void main(String []str){
		
//		int nTotalNoOfDays = 400;
//		int nNoOfYears = nTotalNoOfDays / 365;
//		int nNoOfMonths = (nTotalNoOfDays % 365) /  30;
//		int nNoOfDays = (nTotalNoOfDays % 365) %  30;
//		
		EncryptionUtils eU = new EncryptionUtils();
		String strEU = eU.encrypt("734");
		
		System.out.println("strEU ===>> " + strEU);
		
		String strDEU = eU.decrypt(strEU);
		System.out.println("strDEU ===>> " + strDEU);
		
//		System.out.println("nNoOfYears=="+nNoOfYears);
//		System.out.println("nNoOfMonths=="+nNoOfMonths);
//		System.out.println("nNoOfDays=="+nNoOfDays);2,147,483,647 
//		UtilityFunctions uF = new UtilityFunctions();
//		double d = 234.12413;
//		String dblVal = uF.formatIntoFourDecimalWithOutComma(d);
//	    System.out.println("dblVal ===>> " + dblVal);
	     
//		System.out.println("nNoOfDays=="+Integer.parseInt("214748369"));
		
	}
}
