<script type="text/javascript">

Highcharts.setOptions({
    colors: ['#ff8080','#80ff80', '#8080ff']
});

var chart;

$(document).ready(function() {
   chart = new Highcharts.Chart({
      
	   chart: {
         renderTo: 'containerForWorkingHoursCharts',
         defaultSeriesType: 'column',
       	 plotBorderWidth: 1
      },
      credits: {
          enabled: false
      }, 
      title: {
         text: ' '
      },
      
      xAxis: {
         categories: ['This week','Previous week','Previous to previous week']
      },      
      
      yAxis: {
    	  min: 0,
    	  lineWidth: 2,	//y axis itself
          title: {
             text: ' '
	        }
      },
      credits: {
       	enabled: false
	   },
	   title: {
	 	  		text : '',
	     		floating: true
	   },
      plotOptions: {
    	  column: {
          pointPadding: 0.2,
          borderWidth: 0
       }
      },
     
      <%=request.getAttribute("ServiceWorkingHours")%>
   });
});
</script>

<div id="containerForWorkingHoursCharts" style="height: 200px; width:100%"></div>
