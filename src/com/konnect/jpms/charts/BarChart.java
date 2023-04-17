package com.konnect.jpms.charts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import ChartDirector.ArrayMath;
import ChartDirector.BarLayer;
import ChartDirector.BoxWhiskerLayer;
import ChartDirector.Chart;
import ChartDirector.LegendBox;
import ChartDirector.TextBox;
import ChartDirector.XYChart;

public class BarChart {
	
	public static ArrayList<Double> chardatalist = new ArrayList<Double>();
	public static int nchartCount = 0;
	public static ArrayList<String> empChart = new ArrayList<String>();
	public static List<String> Chartcycle;

	
	
	public BarChart(){
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");
	}
	
	
	public XYChart getChart(){
		XYChart c = null;
		try {
			
			
			double[] data = {85, 156, 179.5, 211, 123};

			// The labels for the bar chart
			String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri"};

			// The colors for the bar chart
			int[] colors = {0xb8bc9c, 0xa0bdc4, 0x999966, 0x333366, 0xc3c3e6};

			// Create a XYChart object of size 300 x 220 pixels. Use golden background color. Use
			// a 2 pixel 3D border.
			c = new XYChart(300, 220, Chart.goldColor(), -1, 2);

			// Add a title box using 10 point Arial Bold font. Set the background color to
			// metallic blue (9999FF) Use a 1 pixel 3D border.
			c.addTitle("Daily Network Load", "Arial Bold", 10).setBackground(Chart.metalColor(
			    0x9999ff), -1, 1);

			// Set the plotarea at (40, 40) and of 240 x 150 pixels in size
			c.setPlotArea(40, 40, 240, 150);

			// Add a multi-color bar chart layer using the given data and colors. Use a 1 pixel
			// 3D border for the bars.
			c.addBarLayer3(data, colors).setBorderColor(-1, 1);

			// Set the labels on the x axis.
			c.xAxis().setLabels(labels);

			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public XYChart getChartWithMarks(double[] barData, double[] markData, String[] labels){
		XYChart c = null;
		try {
			
			
//			double[] barData = {100, 125, 245, 147, 67, 96, 160, 145, 97, 167, 220, 125};
//			double[] markData = {85, 156, 220, 120, 80, 110, 140, 130, 111, 180, 175, 100};
//
//			// The labels for the bar chart
//			String[] labels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept",
//			    "Oct", "Nov", "Dec"};

			// Create a XYChart object of size 480 x 360 pixels. Use a vertical gradient color
			// from pale blue (e8f0f8) to sky blue (aaccff) spanning half the chart height as
			// background. Set border to blue (88aaee). Use rounded corners. Enable soft drop
			// shadow.
			c = new XYChart(480, 360);
//			c.setBackground(c.linearGradientColor(0, 0, 0, c.getHeight() / 2, 0xe8f0f8, 0xaaccff
//			    ), 0x88aaee);
////			c.setRoundedFrame();
//			c.setDropShadow();

			// Add a title to the chart using 15 points Arial Italic font. Set top/bottom margins
			// to 12 pixels.
			TextBox title = c.addTitle("Roster vs Actual Hours", "Arial Italic", 15);
			title.setMargin2(0, 0, 12, 12);

			// Tentatively set the plotarea to 50 pixels from the left edge to allow for the
			// y-axis, and to just under the title. Set the width to 65 pixels less than the
			// chart width, and the height to reserve 90 pixels at the bottom for the x-axis and
			// the legend box. Use pale blue (e8f0f8) background, transparent border, and grey
			// (888888) dotted horizontal grid lines.
//			c.setPlotArea(50, title.getHeight(), c.getWidth() - 65, c.getHeight() -
//			    title.getHeight() - 90, 0xe8f0f8, -1, Chart.Transparent, c.dashLineColor(
//			    0x888888, Chart.DotLine));

			// Add a legend box where the bottom-center is anchored to the 15 pixels above the
			// bottom-center of the chart. Use horizontal layout and 8 points Arial font.
//			LegendBox legendBox = c.addLegend(c.getWidth() / 2, c.getHeight() - 15, false,
//			    "Arial", 8);
//			legendBox.setAlignment(Chart.BottomCenter);

			// Set the legend box background and border to pale blue (e8f0f8) and bluish grey
			// (445566)
//			legendBox.setBackground(0xe8f0f8, 0x445566);

			// Use rounded corners of 5 pixel radius for the legend box
//			legendBox.setRoundedCorners(5);

			// Use line style legend key
//			legendBox.setLineStyleKey();

			// Set axes to transparent
			c.xAxis().setColors(Chart.Transparent);
			c.yAxis().setColors(Chart.Transparent);

			// Set the labels on the x axis
			c.xAxis().setLabels(labels);
			c.xAxis().setLabelStep(3);

			// Add a box-whisker layer with just the middle mark visible for the marks. Use red
			// (ff0000) color for the mark, with a line width of 2 pixels and 10% horizontal gap
			BoxWhiskerLayer markLayer = c.addBoxWhiskerLayer(null, null, null, null, markData,
			    -1, 0xff0000);
			markLayer.setLineWidth(2);
			markLayer.setDataGap(0.1);

			// Add the legend key for the mark line
//			legendBox.addKey("Roster Hours", 0xff0000, 2);

			// Tool tip for the mark layer
			markLayer.setHTMLImageMap("", "", "title='Target at {xLabel}: {med}'");

			// Add a blue (0066cc) bar layer using the given data.
			BarLayer barLayer = c.addBarLayer(barData, 0x0066cc, "Actual Hours");

			// Use soft lighting effect for the bars with light direction from left.
//			barLayer.setBorderColor(Chart.Transparent, Chart.softLighting(Chart.Left));

			// Tool tip for the bar layer
			barLayer.setHTMLImageMap("", "", "title='{dataSetName} at {xLabel}: {value}'");

			// Adjust the plot area size, such that the bounding box (inclusive of axes) is 10
			// pixels from the left edge, just below the title, 15 pixels from the right edge,
			// and 10 pixels above the legend box.
//			c.packPlotArea(10, title.getHeight(), c.getWidth() - 15, c.layoutLegend().getTopY() -
//			    10);


			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public XYChart getMulitCharts(double[] data0, double[] data1, double[] data2, String[] labels){
		XYChart c = null;
		try {
			
			
//			double[] data0 = {100, 125, 245, 147, 67};
//			double[] data1 = {85, 156, 179, 211, 123};
//			double[] data2 = {97, 87, 56, 267, 157};
//			String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri"};

			// Create a XYChart object of size 400 x 240 pixels
			c = new XYChart(260, 160);

			// Add a title to the chart using 10 pt Arial font
//			c.addTitle("         Average Weekday Network Load", "", 10);

			// Set the plot area at (50, 25) and of size 320 x 180. Use two alternative
			// background colors (0xffffc0 and 0xffffe0)
			c.setPlotArea(30, 25, 200, 100, 0xffffc0, 0xffffe0);

			// Add a legend box at (55, 18) using horizontal layout. Use 8 pt Arial font, with
			// transparent background
			c.addLegend(55, 120, false, "", 8).setBackground(Chart.Transparent);

			// Add a title to the y-axis
//			c.yAxis().setTitle("Employees");

			// Reserve 20 pixels at the top of the y-axis for the legend box
			c.yAxis().setTopMargin(10);

			// Set the x axis labels
			c.xAxis().setLabels(labels);

			// Add a multi-bar layer with 3 data sets and 3 pixels 3D depth
			BarLayer layer = c.addBarLayer(Chart.Side, 0);
			layer.addDataSet(data0, 0xff8080, "Pending").setDataLabelStyle("Verdana", 10);
			layer.addDataSet(data1, 0x80ff80, "Approved").setDataLabelStyle("Verdana", 10);
			layer.addDataSet(data2, 0x8080ff, "Denied").setDataLabelStyle("Verdana", 10);

			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	
	public XYChart getEarlyLate(double[] data, String[] labels, String strTitle){
		XYChart c = null;
		try {
			
			
//			double[] data = {-6.3, 2.3, 0.7, -3.4, 2.2, -2.9, -0.1, -0.1, 3.3, 6.2, 4.3, 1.6};

			// The labels for the bar chart
//			String[] labels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
//			    "Oct", "Nov", "Dec"};

			// Create a XYChart object of size 500 x 320 pixels
			c = new XYChart(410, 320);

			// Add a title to the chart using Arial Bold Italic font
			c.addTitle(strTitle, "Arial Bold Italic");

			// Set the plotarea at (50, 30) and of size 400 x 250 pixels
			c.setPlotArea(30, 30, 340, 250);

			// Add a bar layer to the chart using the Overlay data combine method
			BarLayer layer = c.addBarLayer2(Chart.Overlay);

			// Select positive data and add it as data set with blue (6666ff) color
			layer.addDataSet(new ArrayMath(data).selectGEZ(null, Chart.NoValue).result(),
			    0x6666ff);

			// Select negative data and add it as data set with orange (ff6600) color
			layer.addDataSet(new ArrayMath(data).selectLTZ(null, Chart.NoValue).result(),
			    0xff6600);

			// Add labels to the top of the bar using 8 pt Arial Bold font. The font color is
			// configured to be red (0xcc3300) below zero, and blue (0x3333ff) above zero.
			layer.setAggregateLabelStyle("Arial Bold", 8, layer.yZoneColor(0, 0xcc3300, 0x3333ff)
			    );

			// Set the labels on the x axis and use Arial Bold as the label font
			c.xAxis().setLabels(labels).setFontStyle("Arial Bold");
			
			c.xAxis().setLabelStep(4);

			// Draw the y axis on the right of the plot area
			c.setYAxisOnRight(true);

			// Use Arial Bold as the y axis label font
			c.yAxis().setLabelStyle("Arial Bold");

			// Add a title to the y axis
			c.yAxis().setTitle("Time in minutes");

			// Add a light blue (0xccccff) zone for positive part of the plot area
			c.yAxis().addZone(0, 9999, 0xccccff);

			// Add a pink (0xffffcc) zone for negative part of the plot area
			c.yAxis().addZone(-9999, 0, 0xffcccc);
//			c.yAxis().setOffset(100, -100);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	
	
	
	public static  java.awt.Image createChart() 
	{
		
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");
		double []arrAdddata;
		arrAdddata = new double[6];
		double[] arrchartdata = new double[6];
	    String[] labels = new String[6];

		ListIterator<String> litc1 = Chartcycle.listIterator();
		while (litc1.hasNext()) {
			litc1.next();
			}

		int k = 0;
		while (k < 6 && litc1.hasPrevious()) {
			labels[k] = litc1.previous();
			k++;
		}
	
		XYChart c = new XYChart(800, 300);
	
		c.addTitle(" ", "", 10);
		c.setPlotArea(75, 65, 700, 200, 16775416, 16775416);
		c.addLegend(55, 18, false, "Arial Bold", 10).setBackground(0xffffff);
		c.yAxis().setTitle("No of Resources", "Arial Bold", 10);
		c.yAxis().setTopMargin(20);
		c.xAxis().setLabelStyle("Arial Bold", 10);
		c.xAxis().setLabels(labels);
		BarLayer layer = c.addBarLayer2(Chart.Side, 3);
		int col = 0;
		Iterator<String> ite = empChart.iterator();
		while (ite.hasNext()) {
			String strempdemo = ite.next();
			ListIterator<String> litc = Chartcycle.listIterator();
			while (litc.hasNext()) {
				litc.next();
			}
			int i = 0;
			while (i < 6 && litc.hasPrevious()) {
				double f1 = chardatalist.get(nchartCount);
				nchartCount++;
				arrAdddata[i] = f1;
				i++;
			}
			arrchartdata = arrAdddata;

			for (int j = 0; j < arrchartdata.length; j++) {
			}
			if (col == 0) {
				layer.addDataSet(arrchartdata, 0x006699, strempdemo);
			} else if (col == 1) {

				layer.addDataSet(arrchartdata, 0x990000, strempdemo);

			} else if (col == 2) {
				layer.addDataSet(arrchartdata, 0x669900, strempdemo);

			} else if (col==3) {

				layer.addDataSet(arrchartdata, 0x663399, strempdemo);
			}
			
			else if (col==4) {

				layer.addDataSet(arrchartdata, 0x999900, strempdemo);

			}
			
			else if (col==5) {

				layer.addDataSet(arrchartdata, 0x990099, strempdemo);

			}
			else if (col==6) {

				layer.addDataSet(arrchartdata, 0xFF00FF, strempdemo);

			}
			
			else if (col==7) {

				layer.addDataSet(arrchartdata, 0xFF9900, strempdemo);

			}
			
			else if (col==8) {

				layer.addDataSet(arrchartdata, 0x6600cc, strempdemo);

			}
			else if (col==9) {

				layer.addDataSet(arrchartdata, 0x330066, strempdemo);

			}
		
			else if (col==10) {

				layer.addDataSet(arrchartdata, 0xFF0000, strempdemo);

			}else
			{
				layer.addDataSet(arrchartdata, 0x003300, strempdemo);
			}
			
			col++;
			
		}
		nchartCount=0;
		
		
		return  c.makeImage();
	}
	
	
	
	public void clearList() {
		// TODO Auto-generated method stub
		
		
	
		empChart.clear();
		chardatalist.clear();
        nchartCount=0;
        
		
	}

		
	
	public void callPdfChartData(String strEmpName, ArrayList<String> data) {
		empChart.add(strEmpName);
		Iterator<String> it12 = data.iterator();
		while (it12.hasNext()) {
			String straddData = it12.next();
			  if(straddData==null)
			  {
				  straddData="0";
			  }
			  
			  chardatalist.add(Double.parseDouble(straddData));
       
      
		}
	}
	public void callCycle(List<String> alInnerChart)
	{
		this.Chartcycle = alInnerChart;
	}
	
	

}
