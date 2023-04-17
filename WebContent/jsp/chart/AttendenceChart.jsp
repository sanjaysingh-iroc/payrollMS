<%@page import="java.util.ArrayList"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>
<script type="text/javascript">

var alDatesChart = <%= request.getAttribute("alDatesChart")%>;
var alLeaveEntriesChart = <%=request.getAttribute("alLeaveEntriesChart")%>;
var alLateInChart = <%=request.getAttribute("alLateInChart")%>;
var alEarlyOutChart = <%=request.getAttribute("alEarlyOutChart")%>;
var alAbsentChart = <%=request.getAttribute("alAbsentChart")%>;

var chart;
$(document).ready(function() {
   chart = new Highcharts.Chart({
      chart: {
         renderTo: 'Acontainer',
        	defaultSeriesType: 'column'
      },
      title: {
         text: 'Attendence Details'
      },
      credits: {
          enabled: false
      },
      xAxis: {
         categories: alDatesChart
      },
      yAxis: {
         min: 0,
         title: {
            text: 'No of Employees'
         }
      },
      plotOptions: {
         column: {
            pointPadding: 0.2,
            borderWidth: 0
         }
      },
     series: [{
         name: 'Absent',
         data: alAbsentChart
   
      }, {
         name: 'Late In',
         data: alLateInChart
   
      }, {
         name: 'Early Out',
         data: alEarlyOutChart
   
      }, {
         name: 'Leave Requests',
         data: alLeaveEntriesChart
   
      }]
   });
});


</script>

<div id="Acontainer" style="width:100%; height: 200px"></div>