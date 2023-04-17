<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.util.*" %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highstock.js"></script>
<script type="text/javascript">

<%
	ArrayList outer=new ArrayList();
	outer =(ArrayList) request.getAttribute("Data");
%>

var params = new Array(<%
		
		for(int i = 0; i < outer.size(); i++) {
			
			out.print("\""+(String)outer.get(i)+"\"");
		  	if(i+1 < outer.size()) {
		    	out.print(",");
		  	}
		}
		%>);
var paramsSize = <%= outer.size()%>;
alert(paramsSize);

if(paramsSize == 0) {
	alert('No data available!');
}
		
</script>

<script type="text/javascript">

Highcharts.setOptions({
	global: {
		useUTC: false
	}
});

$(function() {
	
	$.get('data.csv', function(csv, state, xhr) {
		
		var data = [], navigatorData = [],x;
		
		
		$.each(params, function(i, line) {

            var point = line.split(';'), date = point[0].split('/');
            var open_time =  point[1].split(':');
            var high_time =  point[2].split(':');
            var low_time =  point[3].split(':');
            var close_time =  point[4].split(':');
            
            //year, month,date 
            x = Date.UTC(date[2], date[1] - 1, date[0]);
            
            open_time_value = Date.UTC(2011, 07, 01, open_time[0],open_time[1], 0);
            high_time_value = Date.UTC(2011, 07, 01, high_time[0],high_time[1], 0);
            low_time_value = Date.UTC(2011, 07, 01, low_time[0],low_time[1], 0);
            close_time_value = Date.UTC(2011, 07, 01, close_time[0],close_time[1], 0);
            
            data.push([
				x, // date 
				//y,//time 
				parseFloat(open_time_value), // open 
				parseFloat(high_time_value), // high 
				parseFloat(low_time_value), // low 
				parseFloat(close_time_value) // close 
			]);
            navigatorData.push([x, parseFloat(point[4])]); // close
            
		});
		
		var xAxisFirstValue;
		
		// create the chart
		var chart = new Highcharts.StockChart({

			chart: {
		        renderTo: 'container',
		        plotBorderWidth: 1
		    },
		    
		    navigator: {
		        series: {
		            data: navigatorData
		        }
		    },
		    
		    rangeSelector: {
		        selected: 0
		    },
		   
		    title: {
		        text: 'Employee Clock Entries'
		    },
		    
		    xAxis: {
			    	type: 'datetime',
			    	showLastLabel : 'true',
			        maxZoom: 14 * 24 * 3600000, // fourteen days
			        
			        title: {
			            text: 'Days inside Pay Cycle'
			        }
		    },

		    yAxis: {
		    	
		    	type: 'datetime',
		       	tickInterval: 3600000,	//1 hour
		    	alternateGridColor: '#FDFFD5',
		    	lineWidth: 2,	//y axis itself
		    	reversed: true,
		    	offset: 0, //to  distance the graph from the Y-axis
		    	startOnTick: true,
		    	showLastLabel: true,
		    	maxPadding: 0.8,
				//keep lables on the left 
		    		 labels: {
                	align: 'right',
                	x: -3,
                	y: 6
            		}
		    	},

    	  tooltip: {
				   	shared: true,
		            formatter: function(){
		                var p = new Date(this.x)+'<br/>';
		                $.each(this.points, function(i, series){
		               
			                var myDateOpen = new Date(series.point.open);
			                var myDateHigh = new Date(series.point.high);	
			                var myDateLow = new Date(series.point.low);
			                var myDateClose = new Date(series.point.close);	
			                 
		                   p += 'Open: ' +(myDateOpen.getHours())+':'+(myDateOpen.getMinutes())+'<br/>'+
		                        'High: ' +(myDateHigh.getHours())+':'+(myDateHigh.getMinutes())+ '<br/>'+
		                        'Low: ' +(myDateLow.getHours())+':'+(myDateLow.getMinutes())+ '<br/>'+
		                        'Close: '+(myDateClose.getHours())+':'+(myDateClose.getMinutes()) +'<br/>';
		                });
		                return p;
		            }
	        },
	        
		    series: [{
		        type: 'candlestick',
		        name: 'Clock Entries',
		        data: data,
			    dataGrouping: {
		        	units: [[
						'week',                         // unit name
						[1]                             // allowed multiples
					], [
						'month',
						[1, 2, 3, 4, 6]
					]]
	       		}
		    }]
		});
	});
});
		
</script>

<div id="container" style="height: 500"></div>