package com.konnect.jpms.test;

import java.text.DecimalFormat;

import com.konnect.jpms.util.UtilityFunctions;

public class test {

	static DecimalFormat twoDecimal = new DecimalFormat("##,##,##0.00");
	public static void main(String args[]) {
//		String strPrice = formatIntoTwoDecimal(123457789);
//		System.out.println("strPrice ===>> " + strPrice);
		UtilityFunctions functions = new UtilityFunctions();
		System.out.println("strPrice ===>> " + functions.formatIntoComma(1234.56));
	}

	public static String formatPrice(double value) {
        DecimalFormat formatter;
        if (value<=99999)
          formatter = new DecimalFormat("##,##,##,##0.00");
        else
            formatter = new DecimalFormat("#,##,##,###.00");

        return formatter.format(value);
    }
	 
	public static String formatIntoTwoDecimal(double dblVal) {
//	    if(dblVal < 1000) {
//	        return decimalFormat("###.##", dblVal);
//	    } else {
//	        double hundreds = dblVal % 1000;
//	        int other = (int) (dblVal / 1000);
//	        return decimalFormat(",###", other) + ',' + decimalFormat("000.##", hundreds);
//	    }
			return twoDecimal.format(dblVal);
	}
	 
	private static String decimalFormat(String pattern, Object value) {
	    return new DecimalFormat(pattern).format(value);
	}
}
