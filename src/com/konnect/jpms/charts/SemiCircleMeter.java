package com.konnect.jpms.charts;

import ChartDirector.AngularMeter;
import ChartDirector.Chart;

public class SemiCircleMeter {
         
	public SemiCircleMeter(){
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");
	}
	
	public AngularMeter getSemiCircleChart(double[] data, String[] labels){
		AngularMeter m = null;
		try {
			 
			double dblTotal =  data[1];
			if(data[0] > data[1]){
				dblTotal =  data[0];
			}
			
			
			int nX1 = (int)(0.3 * dblTotal);
			int nX2 = (int)(0.8 * dblTotal);
			double value = data[0];

	        // Create an AngularMeter object of size 200 x 115 pixels, with silver
	        // background color, black border, 2 pixel 3D border border and rounded
	        // corners
	        m = new AngularMeter(200, 115, 0xffffff, 0xffffff, 0);
//	        m.setRoundedFrame();
	        

	        // Set the meter center at (100, 100), with radius 85 pixels, and span from
	        // -90 to +90 degress (semi-circle)
	        m.setMeter(100, 100, 87, -90, 90); 

	        
	        // Meter scale is 0 - 100, with major tick every 20 units, minor tick every
	        // 10 units, and micro tick every 5 units
	        m.setScale(0, (dblTotal), (int)(dblTotal)/ 5 , (int)(dblTotal)/ 15);

	        // Set 0 - 60 as green (66FF66) zone
	        m.addZone(0, nX1, 0, 87, 0xff6666);

	        // Set 60 - 80 as yellow (FFFF33) zone
	        m.addZone(nX1, nX2, 0, 85, 0xffff33);

	        // Set 80 - 100 as red (FF6666) zone
	        m.addZone(nX2, dblTotal, 0, 87, 0x66ff66);

	        // Add a text label centered at (100, 60) with 12 pts Arial Bold font
	        m.addText(100, 60, "KPI", "Verdana Bold", 12, Chart.TextColor, Chart.Center);

	        // Add a text box at the top right corner of the meter showing the value
	        // formatted to 2 decimal places, using white text on a black background, and
	        // with 1 pixel 3D depressed border
//	        m.addText(156, 8, "P:"+(int)value, "Verdana", 8, 0x000000
//	            ).setBackground(0xffffff, 0, 0);

	        // Add a semi-transparent blue (40666699) pointer with black border at the
	        // specified value
	        
	        if(value>dblTotal){
	        	m.addPointer(dblTotal, 0x40666699, 0x000000);
	        }else{
	        	m.addPointer(value, 0x40666699, 0x000000);
	        }
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return m;
	} 
	
	
	public AngularMeter getSemiCircleChart(double[] data, String[] labels, String strTitle){
		AngularMeter m = null;
		try {
			 
			double dblTotal = data[0] + data[1];
			int nX1 = (int)(0.3 * dblTotal);
			int nX2 = (int)(0.8 * dblTotal);
			double value = data[0];

	        // Create an AngularMeter object of size 200 x 115 pixels, with silver
	        // background color, black border, 2 pixel 3D border border and rounded
	        // corners
	        m = new AngularMeter(200, 130, 0xffffff, 0xffffff, 0);
//	        m.setRoundedFrame();
	        

	        // Set the meter center at (100, 100), with radius 85 pixels, and span from
	        // -90 to +90 degress (semi-circle)
	        m.setMeter(100, 100, 87, -90, 90); 

	        
	        // Meter scale is 0 - 100, with major tick every 20 units, minor tick every
	        // 10 units, and micro tick every 5 units
	        m.setScale(0, (data[0] + data[1]), (int)(data[0] + data[1])/ 5 , (int)(data[0] + data[1])/ 15);

	        // Set 0 - 60 as green (66FF66) zone
	        m.addZone(0, nX1, 0, 87, 0xff6666);

	        // Set 60 - 80 as yellow (FFFF33) zone
	        m.addZone(nX1, nX2, 0, 85, 0xffff33);

	        // Set 80 - 100 as red (FF6666) zone
	        m.addZone(nX2, dblTotal, 0, 87, 0x66ff66);

	        // Add a text label centered at (100, 60) with 12 pts Arial Bold font
	        m.addText(100, 120, strTitle, "Verdana Bold", 9, Chart.TextColor, Chart.Center);

	        // Add a text box at the top right corner of the meter showing the value
	        // formatted to 2 decimal places, using white text on a black background, and
	        // with 1 pixel 3D depressed border
//	        m.addText(156, 8, "P:"+(int)value, "Verdana", 8, 0x000000
//	            ).setBackground(0xffffff, 0, 0);

	        // Add a semi-transparent blue (40666699) pointer with black border at the
	        // specified value
	        
	        if(value>dblTotal){
	        	m.addPointer(dblTotal, 0x40666699, 0x000000);
	        }else{
	        	m.addPointer(value, 0x40666699, 0x000000);
	        }
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return m;
	}
	
	
	public AngularMeter getSemiCircleChartReverse(double[] data, String[] labels, String strTitle){
		AngularMeter m = null;
		try {
			 
			double dblTotal = data[1];
			if(data[0] > data[1]){
				dblTotal =  data[0];
			}
			
			int nX1 = (int)(0.3 * dblTotal);
			int nX2 = (int)(0.8 * dblTotal);
			double value = data[0];

	        // Create an AngularMeter object of size 200 x 115 pixels, with silver
	        // background color, black border, 2 pixel 3D border border and rounded
	        // corners
	        m = new AngularMeter(200, 130, 0xffffff, 0xffffff, 0);
//	        m.setRoundedFrame();
	        

	        // Set the meter center at (100, 100), with radius 85 pixels, and span from
	        // -90 to +90 degress (semi-circle)
	        m.setMeter(100, 100, 87, -90, 90); 

	        
	        // Meter scale is 0 - 100, with major tick every 20 units, minor tick every
	        // 10 units, and micro tick every 5 units
	        m.setScale(0, (dblTotal), (int)(dblTotal)/ 5 , (int)(dblTotal)/ 15);

	        // Set 0 - 60 as green (66FF66) zone
	        m.addZone(0, nX1, 0, 87, 0x66ff66);

	        // Set 60 - 80 as yellow (FFFF33) zone
	        m.addZone(nX1, nX2, 0, 85, 0xffff33);

	        // Set 80 - 100 as red (FF6666) zone
	        m.addZone(nX2, dblTotal, 0, 87, 0xff6666);

	        // Add a text label centered at (100, 60) with 12 pts Arial Bold font
	        m.addText(100, 120, strTitle, "Verdana Bold", 9, Chart.TextColor, Chart.Center);

	        // Add a text box at the top right corner of the meter showing the value
	        // formatted to 2 decimal places, using white text on a black background, and
	        // with 1 pixel 3D depressed border
//	        m.addText(156, 8, "P:"+(int)value, "Verdana", 8, 0x000000
//	            ).setBackground(0xffffff, 0, 0);

	        // Add a semi-transparent blue (40666699) pointer with black border at the
	        // specified value
	        
	        if(value>dblTotal){
	        	m.addPointer(dblTotal, 0x40666699, 0x000000);
	        }else{
	        	m.addPointer(value, 0x40666699, 0x000000);
	        }
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return m;
	}
}
