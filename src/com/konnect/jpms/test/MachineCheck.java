package com.konnect.jpms.test;

import java.net.InetAddress;

public class MachineCheck {
	  
	private boolean checkMachineIP(String str) {
		boolean flag = false;
		InetAddress byName;
		try {
			byName = InetAddress.getByName(""+str);
			System.out.println(byName);
//		    System.out.println(byName.isReachable(1000));
		    flag = byName.isReachable(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return flag;
	}
	
	
	
	static public void  main(String args[]){
//		double x = 4.0666; 
		MachineCheck mc = new MachineCheck();
//		System.out.println("list ==>>>>> " + mc.checkMachineIP("192.168.1.5"));
		mc.formatIntoOneDecimalIfDecimalValIsThere(4.0);
	}
	
	public String formatIntoOneDecimalIfDecimalValIsThere(double dblVal) {
		String strVal = dblVal+"";
		if(strVal!=null && strVal.contains(".") && strVal.indexOf(".")>0) {
			String[] strTmp = strVal.replace(".", ",").split(",");
			
			System.out.println("strVal ===>> " + strVal);
			System.out.println("strTmp ===>> " + strTmp[1]);
		}
		return strVal;
	}
}
