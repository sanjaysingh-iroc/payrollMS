<%@page import="java.util.ArrayList"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>

<script type="text/javascript">
<%
	String sbHeadName  = (String)request.getAttribute("sbHeadName");	
	String sbHeadValue = (String)request.getAttribute("sbHeadValue");
%>

var headName = <%=sbHeadName%>;
var headValue = <%= sbHeadValue%>;

Highcharts.setOptions({
    colors: ['#EE9A00']
});

var chart;
$(document).ready(function() {
   	chart = new Highcharts.Chart({
      chart: {
         renderTo: 'container',
         defaultSeriesType: 'bar',
       	 plotBorderWidth: 1,
       	 plotShadow: true
      },
      title: {
         text: 'Salary Breakup Chart'
      },
      credits: {
       	enabled: false
   	  },
      xAxis: {
         categories: headName
      },
      yAxis: {
         min: 0,
         title: {
            text: 'Amount'
         }
      },
      plotOptions: {
         series: {
            stacking: 'normal'
         }
      },
	series: [
				{
					name: 'Salary Heads',
					data:  headValue
				}
   			]
   });
});

</script>

<div id="container" style="height: 500px;"></div>