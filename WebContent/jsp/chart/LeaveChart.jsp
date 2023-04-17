<%@page import="java.util.ArrayList"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>


<script type="text/javascript">
var alLeaveDetails = <%=request.getAttribute("alLeaveDetails")%>;

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
         text: 'Leave Details'
      },
      credits: {
       	enabled: false
   	  },
   	legend: {
        enabled: false
	},
      xAxis: {
    	  categories: ['Requests','Approved','Pending','Denied']
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
					data:  alLeaveDetails
				}
   			]
   });
});

</script>

<div id="container" style="height: 200px; "></div>