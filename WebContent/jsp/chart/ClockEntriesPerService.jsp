<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.util.*" %>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highstock.js"></script>
<script type="text/javascript">

<%
	//ArrayList outerIn = new ArrayList();
	ArrayList outerIn = (ArrayList)request.getAttribute("outerIn");
	ArrayList outerOut = (ArrayList)request.getAttribute("outerOut");
	// alServiceSize = new ArrayList();
	ArrayList alServiceSizeIn = (ArrayList)request.getAttribute("alServiceSizeIn");
	ArrayList alServiceSizeOut = (ArrayList)request.getAttribute("alServiceSizeOut");
	//int ServicesCount = Integer.parseInt((String)request.getAttribute("ServicesCount"));
	int ServicesCount = alServiceSizeIn.size(); 
%>
	<%-- var params = new Array(<%
			
			for(int i = 0; i < outer.size(); i++) {
				out.print("\""+(String)outer.get(i)+"\"");
			  	if(i+1 < outer.size()) {
			    	out.print(",");
			  	}
			}
	%>); --%>
	
	<%
	/* for(int i = 0; i < outerIn.size(); i++) {
		
		if( ((ArrayList)outerIn.get(i)).size() != 0 ) {
		
			for(int j=0; j<((ArrayList)outerIn.get(i)).size(); j++) {
				
				System.out.print("\""+(String)((ArrayList)outerIn.get(i)).get(j)+"\"");
			    System.out.print(","); 
			}
		}
	}
	
	int i;
	for(i = 0; i < outerOut.size() ; i++) {
		
		if( ((ArrayList)outerOut.get(i)).size() != 0 ) {
			
			int j;
			for(j=0; j<((ArrayList)outerOut.get(i)).size(); j++) {
				
				System.out.print("\""+(String)((ArrayList)outerOut.get(i)).get(j)+"\"");
				
				if(((ArrayList)outerOut.get(i+1)).size() != 0) {
	  				System.out.print(",");
				}
				
	  		}
		}
	} */
	
	%>
	
	var params = new Array(<%
	int i;	
	for(i = 0; i < outerIn.size(); i++) {
		
		for(int j=0; j<((ArrayList)outerIn.get(i)).size(); j++) {
			
			out.print("\""+(String)((ArrayList)outerIn.get(i)).get(j)+"\"");
			out.print(","); 
		}
	}
	
	for(i = 0; i < outerOut.size() ; i++) {
		
		int j;
		for(j=0; j<((ArrayList)outerOut.get(i)).size(); j++) {
			
			out.print("\""+(String)((ArrayList)outerOut.get(i)).get(j)+"\"");
			System.out.print("\""+(String)((ArrayList)outerOut.get(i)).get(j)+"\"");
			
			if(!(i==outerOut.size()-1 && j==((ArrayList)outerOut.get(i)).size() - 1)) {		//Not the last element
	
				out.print(",");
				System.out.print(",");
			}
  				
  		}
		
	}
	%>);
	
	
</script>

<script type="text/javascript">

Highcharts.setOptions({
	global: {
		useUTC: false
	}
});

$(function() {
	
	$.get('data.csv', function(csv, state, xhr) {
		
			//var data = [], data1 = [], series_name;
			
			<% for(i=0; i<alServiceSizeIn.size(); i++) { %>
				var service<%=i%>InSize = <%=alServiceSizeIn.get(i)%>;
				var data<%=i%>In = [];
				var series_name<%=i%>In;
			<%}%>
			
			<% for(i=0; i<alServiceSizeOut.size(); i++) { %>
				var service<%=i%>OutSize = <%=alServiceSizeOut.get(i)%>;
				var data<%=i%>Out = [];
				var series_name<%=i%>Out;
			<%}
			
			%>
			
			$.each(params, function(i, line) {
			    
		            var point = line.split(';'), 
						date = point[1].split('/');
						time = point[2].split(':');
					var flag = false;
					if (point.length > 1) {
						
		                x = Date.UTC(date[2], date[1] - 1, date[0]);
		                value = Date.UTC(2011, 07, 01, time[0], time[1], 0);
					
		                if(point[3] == "IN") {
							
			                <% 
			                	for(int h=0; h<ServicesCount; h++) {	
			                %>
			                		if(flag == false) {
			                			
				                		if(i < <%= alServiceSizeIn.get(h) %> ) {
				                			
				                			
						                	data<%=h%>In.push([
														x, // time 
														parseFloat(value) // close 
													]);
						                	series_name<%=h%>In = point[0];
						                	
						                	flag = true;  //break the loop 
						                	
					                	} else {
					                		
					                		//go to next service 
					                	}
			                		}
		                	<%
			                }%>
		                		
		                }else {					
				                //repeat same thing for out 
				                
				                <% 
			                	for(int h=0; h<ServicesCount; h++) {	
			                	%>
			                		if(flag == false) {
			                			
				                		if(i < <%= alServiceSizeOut.get(h) %> ) {
				                			
						                	data<%=h%>Out.push([
														x, // time 
														parseFloat(value) // close 
													]);
						                	series_name<%=h%>Out = point[0];
						                	
						                	flag = true;  //break the loop 
						                	
					                	} else {
					                		
					                		//go to next service 
					                	}
			                		}
			                	<%
				                }%>
		                	}
                	}
		                //series_name = point[0]; 
			});
				
		// Create the chart	
		window.chart = new Highcharts.StockChart({
		    chart: {
		        renderTo: 'container',
	        	plotBorderWidth: 1
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
	            },
	        	enabled: false
        },
	        title: {
	   	  		text : '',
	       		floating: true
	     	},
		    xAxis: {
		        maxZoom: 14 * 24 * 3600000 // fourteen days
		    },
		    yAxis: {
		    	type: 'datetime',
		    	tickInterval: 3600000,	//1 day
		    	lineWidth: 2,	//y axis itself
		    	reversed: true,
		    	alternateGridColor: '#FDFFD5',
		    	//keep lables on the left 
	    		labels: {
	           		align: 'right',
           			x: -3,
           			y: 6
       				}
	    		},
		    series: [
						<% for(int h=0; h<ServicesCount; h++) { %>
			             	{
						    	name: series_name<%=h%>In,
						        data: data<%=h%>In,
						        lineWidth: 0,
						        marker: {
						        	enabled: true,
						        	radius: 5
						        }
				    		},
			    		<%}%>
			    		<% for(int h=0; h<ServicesCount; h++) { %>
			             	{
						    	name: series_name<%=h%>Out,
						        data: data<%=h%>Out,
						        lineWidth: 0,
						        marker: {
						        	enabled: true,
						        	radius: 5
						        }
				    		},
		    			<%}%>
					]
		});
	});
});

</script>

<div id="container" style="height: 500"></div>