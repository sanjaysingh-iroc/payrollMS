package com.konnect.jpms.charts;

import ChartDirector.Chart;
import ChartDirector.LinearMeter;

public class LinearZMeter {
         
	public LinearZMeter(){
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");
	}
	
	public LinearMeter getLinearChart(double[] data, String[] labels){
		LinearMeter m = null;
		try {
			
		double dblTotal = data[0] + data[1];
		
		if(data[0] > data[1]){
			dblTotal =  data[0];
		}
		
		int nX1 = (int)(0.3 * dblTotal);
		int nX2 = (int)(0.8 * dblTotal);
		double value = data[0];

		// Create an LinearMeter object of size 210 x 45 pixels, using silver background with
		// a 2 pixel black 3D depressed border.
		m = new LinearMeter(190, 20, Chart.silverColor(), 0, 0);

		// Set the scale region top-left corner at (5, 5), with size of 200 x 20 pixels. The
		// scale labels are located on the bottom (implies horizontal meter)
		m.setMeter(0, 0, 190, 20, Chart.Bottom);

		// Set meter scale from 0 - 100
		m.setScale(0, dblTotal);

		// Add a title at the bottom of the meter with a 1 pixel raised 3D border
//		m.addTitle2(Chart.Bottom, "Battery Level", "Arial Bold", 8).setBackground(
//		    Chart.Transparent, -1, 1);

		// Set 3 zones of different colors to represent Good/Weak/Bad data ranges
		m.addZone(nX2, dblTotal, 0x66ff66, "Good");
		m.addZone(nX1, nX2, 0xffff33, "Average");
		m.addZone(0, nX1, 0xff6666, "Poor");

		// Add empty labels (just need the ticks) at 0/20/50/80 as separators for zones
//		m.addLabel(0, " ");
//		m.addLabel(20, " ");
//		m.addLabel(50, " ");
//		m.addLabel(100, " ");

		// Add a semi-transparent blue (800000ff) pointer at the specified value, using
		// triangular pointer shape
		m.addPointer(value, 0x800000ff).setShape(Chart.TriangularPointer);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}
}
