package com.konnect.jpms.charts;

import ChartDirector.Chart;
import ChartDirector.PieChart;

public class PieCharts {
         
	public PieCharts(){
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");
	}
	
	public PieChart get3DPieChart(double[] data, String[] labels){
		PieChart c = null;
		try {
			
//			
//			double[] data = {72, 18, 15, 12};

			// The labels for the pie chart
//			String[] labels = {"Labor", "Machinery", "Facilities", "Computers"};

			// The depths for the sectors
//			double[] depths = {10, 20, 10, 10};
			int depth = 10;



			// Create a PieChart object of size 360 x 300 pixels, with a light blue (DDDDFF)
			// background and a 1 pixel 3D border
			c = new PieChart(260, 160, 0xffffff, -1, 0);

			// Set the center of the pie at (180, 175) and the radius to 100 pixels
			c.setPieSize(120, 95, 70);

			// Add a title box using 15 pts Times Bold Italic font and blue (AAAAFF) as
			// background color
//			c.addTitle("Attendence", "Times New Roman Bold Italic", 12
//			    ).setBackground(0xaaaaff);

			// Set the pie data and the pie labels
			c.setData(data, labels);
//			c.setLabelStyle("Verdana", 12, Chart.Transparent);
			c.setLabelStyle("Verdana", 10, 0x000000);



			// Draw the pie in 3D with variable 3D depths
//			c.set3D2(depths);
			c.set3D(depth);

			// Set the start angle to 225 degrees may improve layout when the depths of the
			// sector are sorted in descending order, because it ensures the tallest sector is at
			// the back.
			c.setStartAngle(225);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
}
