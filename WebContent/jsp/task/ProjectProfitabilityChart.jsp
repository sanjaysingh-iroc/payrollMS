<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
 <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> 
 <script type="text/javascript" src="scripts/charts/highcharts.js"></script> 

<%-- <% String sbProfitC = (String)request.getAttribute("sbProfitC"); 
	System.out.println("sbProfitC =====>> " + sbProfitC);
%> --%>
 <script type="text/javascript">
Highcharts.setOptions({
    colors: ['#ff8080','#80ff80', '#8080ff']
});

var chart;

$(document).ready(function() {
	chart = new Highcharts.Chart({
		chart: {
			renderTo: 'CEStatus',
			type: 'bar'
		},
		title: {
			text: ''
		},
		
		xAxis: {
			categories: [
				"Profitability"
			]
		},
		yAxis: {
			/* min: 0, */
			title: {
				text: 'Rs.'
			}
		},
	      credits: {
	         	enabled: false
	  	   },
		legend: {
			//layout: 'vertical',
			//backgroundColor: '#FFFFFF',
			//align: 'left',
			//verticalAlign: 'bottom',
			//x: 50,
			//y: 0,
			//floating: true,
			//shadow: true
			//height: 150,
			reversed: true
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
			series: [<%=request.getAttribute("sbProfitC")%>]
	});
});


</script>

<div id="CEStatus" style="height: 900px; width:100%;float:left"></div> 


