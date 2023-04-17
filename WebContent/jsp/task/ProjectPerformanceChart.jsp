<%@page import="java.util.ArrayList"%>


<%-- <script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>

<script type="text/javascript">
Highcharts.setOptions({
    colors: ['#ff8080','#80ff80', '#8080ff']
});

var chart;

$(document).ready(function() {
	chart = new Highcharts.Chart({
		chart: {
			renderTo: 'CEStatus',
			type: 'column'
		},
		title: { 
			text: ''
		},
		
		xAxis: {
			categories: [
<%=request.getAttribute("pro_name")%>
			]
		},
		yAxis: {
			min: 0,
			title: {
				text: 'Rs.'
			}
		},
	      credits: {
	         	enabled: false
	  	   },
		legend: {
			layout: 'vertical',
			backgroundColor: '#FFFFFF',
			align: 'left',
			verticalAlign: 'top',
			x: 100,
			y: 70,
			floating: true,
			shadow: true
		},
		tooltip: {
			formatter: function() {
				return ''+
					this.x +': '+ this.y +'';
			}
		},
		plotOptions: {
			column: {
				pointPadding: 0.2,
				borderWidth: 0
			}
		},
			series: [ { name: 'Billable Cost',
		          data: [<%=request.getAttribute("billable_amount")%>]
		    
		       }, { 
		          name: 'Budgeted Cost',
		          data: [<%=request.getAttribute("budgeted_cost")%>]
		       
		       }, { 
		           name: 'Actual Cost',
		           data: [<%=request.getAttribute("actual_amount")%>]
		        
		        }]
	});
});

</script>

<div id="CEStatus" style="height: 200px;width:100%;float:left"></div>  --%>

<!-- <link rel="stylesheet" href="scripts/D3/amcharts/style.css" type="text/css"> -->
        <script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
        <script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script>

        <script>
            var chart;

            var chartData = [<%=request.getAttribute("projectPerformance")%>];
            configChart = function() {
            /* AmCharts.ready(function () { */
                // SERIAL CHART
                chart = new AmCharts.AmSerialChart();
                chart.dataProvider = chartData;
                chart.categoryField = "project";
                chart.startDuration = 1;

                // AXES
                // category
               /*  var categoryAxis = chart.categoryAxis;
                categoryAxis.labelRotation = 90;
                categoryAxis.gridPosition = "start"; */
                
             // AXES
                // Category
                var categoryAxis = chart.categoryAxis;
                categoryAxis.gridAlpha = 0.07;
                categoryAxis.axisColor = "#DADADA";
                categoryAxis.startOnAxis = true;

                // Value
                var valueAxis = new AmCharts.ValueAxis();
                valueAxis.title = "Amount"; // this line makes the chart "stacked"
                //valueAxis.stackType = "100%";
                valueAxis.gridAlpha = 0.07;
                chart.addValueAxis(valueAxis);

                // value
                // in case you don't want to change default settings of value axis,
                // you don't need to create it, as one value axis is created automatically.

                // GRAPH
                var graph = new AmCharts.AmGraph();
                graph.valueField = "Billable Cost";
                graph.balloonText = "[[category]]: Billable Cost:<b>[[value]]</b>";
                graph.type = "column";
                graph.lineAlpha = 0;
                graph.fillAlphas = 0.8;
                chart.addGraph(graph);
		
		 		var graph1 = new AmCharts.AmGraph();
                graph1.valueField = "Budgeted Cost";
                graph1.balloonText = "[[category]]: Budgeted Cost:<b>[[value]]</b>";
                graph1.type = "column";
                graph1.lineAlpha = 0;
                graph1.fillAlphas = 0.8;
                chart.addGraph(graph1);
                
                var graph2 = new AmCharts.AmGraph();
                graph2.valueField = "Actual Cost";
                graph2.balloonText = "[[category]]: Actual Cost:<b>[[value]]</b>";
                graph2.type = "column";
                graph2.lineAlpha = 0;
                graph2.fillAlphas = 0.8;
                chart.addGraph(graph2);

                // CURSOR
                var chartCursor = new AmCharts.ChartCursor();
                chartCursor.cursorAlpha = 0;
                chartCursor.zoomable = false;
                chartCursor.categoryBalloonEnabled = false;
                chart.addChartCursor(chartCursor);

                chart.creditsPosition = "top-right";

                chart.write("CEStatus");
            };
            if (AmCharts.isReady) {
                configChart();
              } else {
                AmCharts.ready(configChart);
              }
        </script>

<div id="CEStatus" style="height: 200px;width:100%;float:left"></div>
