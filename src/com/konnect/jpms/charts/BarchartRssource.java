package com.konnect.jpms.charts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ChartDirector.BarLayer;
import ChartDirector.Chart;
import ChartDirector.MultiChart;
import ChartDirector.XYChart;

public class BarchartRssource {

	public static List<String> paycycle;
	public static List<String> userData = new ArrayList<String>();
	public static int nNumcount = 0;
	public static ArrayList<String> subHead = new ArrayList<String>();
	
	public java.awt.Image createChart() {
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");
		String[] labels = new String[6];
		ListIterator<String> litc1 = paycycle.listIterator();
		while (litc1.hasNext()) {
			litc1.next();
		}
		int k = 0;
		while (k < 6 && litc1.hasPrevious()) {
			labels[k] = "PayCycle" + litc1.previous();
			k++;
		}
		XYChart c = new XYChart(460, 340);
		c.addTitle("        Roster Hours", "Arial Bold", 10);
		c.setPlotArea(65, 25, 390, 250, 0xffffff, 0xffffff);
		c.addLegend(65, 25, false, "Arial Bold", 10).setBackground(16777215);
		c.yAxis().setTitle("Resource Efforts", "Arial Bold", 10);
		c.yAxis().setTopMargin(20);
		c.xAxis().setLabelStyle("Arial Bold", 7);
		c.xAxis().setLabels(labels);
		XYChart c2 = new XYChart(460, 340);
		c2.addTitle("         Actual Hours ", "Arial Bold", 10);
		c2.setPlotArea(55, 25, 390, 250, 0xffffff, 0xffffff);
		c2.addLegend(55, 25, false, "Arial Bold", 10).setBackground(16777215);
		c2.yAxis().setTitle("Resource Efforts", "Arial Bold", 10);
		c2.yAxis().setTopMargin(20);
		c2.xAxis().setLabelStyle("Arial Bold", 7);
		c2.xAxis().setLabels(labels);
		BarLayer layer1 = c2.addBarLayer2(Chart.Side, 3);
		BarLayer layer = c.addBarLayer2(Chart.Side, 3);
		double []arrActual = new double[6];
		double []arrRoster = new double[6];
		int num = 0;
		

		Iterator<String> ite = subHead.iterator();

		while (ite.hasNext()) {
			String empdemo = ite.next();

			int i = 0;
			while (i < 6) {
			
				String strActuala = userData.get(nNumcount);
				String strRoster = userData.get(++nNumcount);
				nNumcount++;

				if (strActuala == null) {
					strActuala = "0";
				}
				if (strRoster == null) {
					strRoster = "0";
				}
				
				double dblActual = Double.parseDouble(strActuala);
				double dblRoster= Double.parseDouble(strRoster);

				arrRoster[i] = dblActual;

				arrActual[i] = dblRoster;

				i++;
			}

			if (num == 0) {
				layer.addDataSet(arrActual, 0x006699, empdemo);
				layer1.addDataSet(arrRoster, 0x006699, empdemo);
			}

			else if (num == 1) {
				layer.addDataSet(arrActual, 0x990000, empdemo);
				layer1.addDataSet(arrRoster, 0x990000, empdemo);
			} else if (num == 2) {
				layer.addDataSet(arrActual, 0x669900, empdemo);
				layer1.addDataSet(arrRoster, 0x669900, empdemo);
			} else if (num == 3) {
				layer.addDataSet(arrActual, 0x663399, empdemo);
				layer1.addDataSet(arrRoster, 0x663399, empdemo);
			} else if(num == 4){
				layer.addDataSet(arrActual, 0xff8080, empdemo);
				layer1.addDataSet(arrRoster, 0x8080ff, empdemo);}
			else if (num==5) {
			
				layer.addDataSet(arrActual, 0x990099, empdemo);
				layer1.addDataSet(arrRoster, 0x990099, empdemo);

			}
			else if (num==6) {


				layer.addDataSet(arrActual, 0xFF00FF, empdemo);
				layer1.addDataSet(arrRoster, 0xFF00FF, empdemo);

			}
			else if (num==7) {

				layer.addDataSet(arrActual, 0xFF9900, empdemo);
				layer1.addDataSet(arrRoster, 0xFF9900, empdemo);
			}
			
			else if (num==8) {
				layer.addDataSet(arrActual, 0x6600cc, empdemo);
				layer1.addDataSet(arrRoster, 0x6600cc, empdemo);
			}
			else if (num==9) {
				layer.addDataSet(arrActual, 0x330066, empdemo);
				layer1.addDataSet(arrRoster, 0x330066, empdemo);
			}
			else if (num==10) {
				layer.addDataSet(arrActual, 0xFF0000, empdemo);
				layer1.addDataSet(arrRoster, 0xFF0000, empdemo);

			}else
			{
				layer.addDataSet(arrActual, 0x003300, empdemo);
				layer1.addDataSet(arrRoster, 0x003300, empdemo);

			}
			num++;
		}
		nNumcount = 0;
		MultiChart m = new MultiChart(1000, 360);
		m.addChart(470, 40, c);
		m.addChart(5, 40, c2);
		return m.makeImage();
	}
	
	public void clearList() {
		// TODO Auto-generated method stub
		
		subHead.clear();
		
		userData.clear();
		nNumcount=0;
	}

	public void call2(String strEmpName, ArrayList<String> data) {

		
		subHead.add(strEmpName);
		
		Iterator<String> it12 = data.iterator();
		while (it12.hasNext()) {
			String strdata = it12.next();
			if (strdata == null || strdata == "") {
				strdata = "0";
			}
			userData.add(strdata);
		

		}

	}
	

	public void callChart() {
			}

	public void callCycle(List<String> alInnerChart,
			List<String> alInnerChart1, List<String> alInnerChart2, String rType) {
				this.paycycle = alInnerChart1;
				
	}
	
	
	
	
}
