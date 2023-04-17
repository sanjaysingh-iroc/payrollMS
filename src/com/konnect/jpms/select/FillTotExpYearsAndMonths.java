package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillTotExpYearsAndMonths {

	
	int totExpID;
	String totExpName;
	
	private FillTotExpYearsAndMonths(int totExpID, String totExpName) {
		this.totExpID = totExpID;
		this.totExpName = totExpName;
	}
	
	public FillTotExpYearsAndMonths() {
	}
	
	
	public List<FillTotExpYearsAndMonths> fillTotExpMonths(){
		List<FillTotExpYearsAndMonths> al = new ArrayList<FillTotExpYearsAndMonths>();
	
		try {
			al.add(new FillTotExpYearsAndMonths(1, "1 Month"));
			al.add(new FillTotExpYearsAndMonths(2, "2 Months"));
			al.add(new FillTotExpYearsAndMonths(3, "3 Months"));
			al.add(new FillTotExpYearsAndMonths(4, "4 Months"));
			al.add(new FillTotExpYearsAndMonths(5, "5 Months"));
			al.add(new FillTotExpYearsAndMonths(6, "6 Months"));
			al.add(new FillTotExpYearsAndMonths(7, "7 Months"));
			al.add(new FillTotExpYearsAndMonths(8, "8 Months"));
			al.add(new FillTotExpYearsAndMonths(9, "9 Months"));
			al.add(new FillTotExpYearsAndMonths(10, "10 Months"));
			al.add(new FillTotExpYearsAndMonths(11, "11 Months"));
//			al.add(new FillTotExpYearsAndMonths(2, "12 Years"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	public List<FillTotExpYearsAndMonths> fillTotExpYears(){
		List<FillTotExpYearsAndMonths> al = new ArrayList<FillTotExpYearsAndMonths>();
	
		try {
			
			al.add(new FillTotExpYearsAndMonths(1, "1 Year"));
			al.add(new FillTotExpYearsAndMonths(2, "2 Years"));
			al.add(new FillTotExpYearsAndMonths(3, "3 Years"));
			al.add(new FillTotExpYearsAndMonths(4, "4 Years"));
			al.add(new FillTotExpYearsAndMonths(5, "5 Years"));
			al.add(new FillTotExpYearsAndMonths(6, "6 Years"));
			al.add(new FillTotExpYearsAndMonths(7, "7 Years"));
			al.add(new FillTotExpYearsAndMonths(8, "8 Years"));
			al.add(new FillTotExpYearsAndMonths(9, "9 Years"));
			al.add(new FillTotExpYearsAndMonths(10, "10 Years"));
			al.add(new FillTotExpYearsAndMonths(11, "11 Years"));
			al.add(new FillTotExpYearsAndMonths(12, "12 Years"));
			al.add(new FillTotExpYearsAndMonths(13, "13 Years"));
			al.add(new FillTotExpYearsAndMonths(14, "14 Years"));
			al.add(new FillTotExpYearsAndMonths(15, "15 Years"));
			al.add(new FillTotExpYearsAndMonths(16, "16 Years"));
			al.add(new FillTotExpYearsAndMonths(17, "17 Years"));
			al.add(new FillTotExpYearsAndMonths(18, "18 Years"));
			al.add(new FillTotExpYearsAndMonths(19, "19 Years"));
			al.add(new FillTotExpYearsAndMonths(20, "20 Years"));
			al.add(new FillTotExpYearsAndMonths(21, "21 Year"));
			al.add(new FillTotExpYearsAndMonths(22, "22 Years"));
			al.add(new FillTotExpYearsAndMonths(23, "23 Years"));
			al.add(new FillTotExpYearsAndMonths(24, "24 Years"));
			al.add(new FillTotExpYearsAndMonths(25, "25 Years"));
			al.add(new FillTotExpYearsAndMonths(26, "26 Years"));
			al.add(new FillTotExpYearsAndMonths(27, "27 Years"));
			al.add(new FillTotExpYearsAndMonths(28, "28 Years"));
			al.add(new FillTotExpYearsAndMonths(29, "29 Years"));
			al.add(new FillTotExpYearsAndMonths(30, "30 Years"));
			al.add(new FillTotExpYearsAndMonths(31, "31 Year"));
			al.add(new FillTotExpYearsAndMonths(32, "32 Years"));
			al.add(new FillTotExpYearsAndMonths(33, "33 Years"));
			al.add(new FillTotExpYearsAndMonths(34, "34 Years"));
			al.add(new FillTotExpYearsAndMonths(35, "35 Years"));
			al.add(new FillTotExpYearsAndMonths(36, "36 Years"));
			al.add(new FillTotExpYearsAndMonths(37, "37 Years"));
			al.add(new FillTotExpYearsAndMonths(38, "38 Years"));
			al.add(new FillTotExpYearsAndMonths(39, "39 Years"));
			al.add(new FillTotExpYearsAndMonths(40, "40 Years"));
			al.add(new FillTotExpYearsAndMonths(41, "41 Year"));
			al.add(new FillTotExpYearsAndMonths(42, "42 Years"));
			al.add(new FillTotExpYearsAndMonths(43, "43 Years"));
			al.add(new FillTotExpYearsAndMonths(44, "44 Years"));
			al.add(new FillTotExpYearsAndMonths(45, "45 Years"));
			al.add(new FillTotExpYearsAndMonths(46, "46 Years"));
			al.add(new FillTotExpYearsAndMonths(47, "47 Years"));
			al.add(new FillTotExpYearsAndMonths(48, "48 Years"));
			al.add(new FillTotExpYearsAndMonths(49, "49 Years"));
			al.add(new FillTotExpYearsAndMonths(50, "50 Years"));
			al.add(new FillTotExpYearsAndMonths(51, "51 Year"));
			al.add(new FillTotExpYearsAndMonths(52, "52 Years"));
			al.add(new FillTotExpYearsAndMonths(53, "53 Years"));
			al.add(new FillTotExpYearsAndMonths(54, "54 Years"));
			al.add(new FillTotExpYearsAndMonths(55, "55 Years"));
			al.add(new FillTotExpYearsAndMonths(56, "56 Years"));
			al.add(new FillTotExpYearsAndMonths(57, "57 Years"));
			al.add(new FillTotExpYearsAndMonths(58, "58 Years"));
			al.add(new FillTotExpYearsAndMonths(59, "59 Years"));
			al.add(new FillTotExpYearsAndMonths(60, "60 Years"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public int getTotExpID() {
		return totExpID;
	}

	public void setTotExpID(int totExpID) {
		this.totExpID = totExpID;
	}

	public String getTotExpName() {
		return totExpName;
	}

	public void setTotExpName(String totExpName) {
		this.totExpName = totExpName;
	}

}  
