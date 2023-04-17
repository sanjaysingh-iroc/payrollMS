<script type="text/javascript">

Highcharts.setOptions({
    colors: ['#00FF00','#FF6633']
});

var chart;
 
$(document).ready(function() {
	   chart = new Highcharts.Chart({
	      
		   chart: {
		        renderTo: 'containerForAttendanceKPICharts',
		        type: 'gauge',
		        plotBackgroundColor: null,
		        plotBackgroundImage: null,
		        plotBorderWidth: 0,
		        plotShadow: false
		    },
		    
		    title: {
		        text: '<%=(String)request.getAttribute("KPI_HEADING")%>'
		    },
		    
		    pane: {
		        startAngle: -120,
		        endAngle: 120,
		        background: [{
		            backgroundColor: {
		                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
		                stops: [
		                    [0, '#FFF'],
		                    [1, '#333']
		                ]
		            },
		            borderWidth: 0,
		            outerRadius: '109%'
		        }, {
		            backgroundColor: {
		                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
		                stops: [
		                    [0, '#333'],
		                    [1, '#FFF']
		                ]
		            },
		            borderWidth: 1,
		            outerRadius: '107%'
		        }, {
		            // default background
		        }, {
		            backgroundColor: '#DDD',
		            borderWidth: 0,
		            outerRadius: '105%',
		            innerRadius: '103%'
		        }]
		    },
		       
		    // the value axis
		    yAxis: {
		        min: 0,
		        max: <%=(String)request.getAttribute("KPI_MAX")%>,
		        
		        minorTickInterval: 'auto',
		        minorTickWidth: 1,
		        minorTickLength: 10,
		        minorTickPosition: 'inside',
		        minorTickColor: '#666',
		
		        tickPixelInterval: 30,
		        tickWidth: 2,
		        tickPosition: 'inside',
		        tickLength: 10,
		        tickColor: '#666',
		        labels: {
		            step: 2,
		            rotation: 'auto'
		        },
		        title: {
		            text: ''
		        },
		        plotBands: [{
		            from: 0,
		            to: <%=(String)request.getAttribute("KPI_1")%>,
		            color: '#DF5353' // green
		        }, {
		            from: <%=(String)request.getAttribute("KPI_1")%>,
		            to: <%=(String)request.getAttribute("KPI_2")%>,
		            color: '#DDDF0D' // yellow
		        }, {
		            from: <%=(String)request.getAttribute("KPI_2")%>,
		            to: <%=(String)request.getAttribute("KPI_MAX")%>,
		            color: '#55BF3B' // red
		        }]        
		    },
		
		    series: [{
		        name: '<%=(String)request.getAttribute("KPI_PREFIX")%>',
		        data: [<%=(String)request.getAttribute("KPI_W")%>],
		        tooltip: {
		            valueSuffix: ' <%=(String)request.getAttribute("KPI_SUFFIX")%>'
		        }
		    }]
		
		});
});
</script>

<div id="containerForAttendanceKPICharts" style="height: 200px; width:100%"></div>