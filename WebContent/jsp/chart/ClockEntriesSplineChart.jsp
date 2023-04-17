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
		
</script>

<script type="text/javascript">

$(function() {
	
	$.get('data.csv', function(csv, state, xhr) {
		
		var data1 = [], navigatorData = [],x;
		var data2 = [], data3 = [], data4 = [];
		
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
            
            data1.push([
				x, // date
				(open_time_value), // open
			]);
            
            data2.push([
        				x, // date
      					(high_time_value), // high
        			]);
            
            data3.push([
        				x, // date
						(low_time_value), // low
        			]);
            
            data4.push([
        				x, // date
        				(close_time_value) // close
        			]);
            
            navigatorData.push([x,(close_time_value)]); // close
            
		});
		
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
		    
		    credits: {
	         	enabled: false
	     	},
	     	
        	legend: {
	            enabled: true,
	            align: 'center',
	            backgroundColor: '#FCFFC5',
	            borderColor: 'black',
	            borderWidth: 2,
	            layout: 'vertical',
	            verticalAlign: 'top',
	            shadow: true,
	            floating: false,
	            layout: "horizontal"
	        },
	        
		    rangeSelector: {
		        selected: 0
		    },
		    
		    title: {
		        text: 'Employee Clock Entries',
	        	floating: true
		    },
		    
		    xAxis: {
			    	type: 'datetime',
			    	showLastLabel : 'true',
			        maxZoom: 14 * 24 * 3600000,  // fourteen days
			        title: {
			            text: 'Days inside Pay Cycle'
			        }
		    },

		    yAxis: {
		    	type: 'datetime',
		    	tickInterval: 3600000,	//1 hour
		    	lineWidth: 2,	//y axis itself
		    	alternateGridColor: '#FDFFD5',
		    	reversed: true,
		    	//keep lables on the left 
	    		labels: {
           				align: 'right',
           				x: -3,
           				y: 6
       					 }
	    	},
	    	
		    tooltip: {
	            backgroundColor: {
	                linearGradient: [0, 0, 0, 100],
	                stops: [
	                    [0, 'white'],
	                    [1, 'silver']
	                ]
	            },
	            borderColor: 'gray',
	            borderWidth: 1,
	            enabled : false
	        },
	        
		    series: [{
		        type: 'spline',
		        name: 'Actual Start Time',
		        data: data1,
			    dataGrouping: {
		        	units: [[
						'week',                         // unit name
						[1]                             // allowed multiples
					], [
						'month',
						[1, 2, 3, 4, 6]
					]]
	       		}
		    },
		    {
		        type: 'spline',
		        name: 'Roster Start Time',
		        data: data2,
			    dataGrouping: {
		        	units: [[
						'week',                         // unit name
						[1]                             // allowed multiples
					], [
						'month',
						[1, 2, 3, 4, 6]
					]]
	       		}
		    },
		    {
		        type: 'spline',
		        name: 'Roster End Time',
		        data: data3,
			    dataGrouping: {
		        	units: [[
						'week',                         // unit name
						[1]                             // allowed multiples
					], [
						'month',
						[1, 2, 3, 4, 6]
					]]
	       		}
		    },
		    {
		        type: 'spline',
		        name: 'Actual End Time',
		        data: data4,
			    dataGrouping: {
		        	units: [[
						'week',                         // unit name
						[1]                             // allowed multiples
					], [
						'month',
						[1, 2, 3, 4, 6]
					]]
	       		}
		    }
		    ]
		});
	});
});
		
</script>

<div id="container" style="height: 500"></div>