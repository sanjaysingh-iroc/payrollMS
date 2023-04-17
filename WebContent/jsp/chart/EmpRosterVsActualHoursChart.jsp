<script type="text/javascript">

Highcharts.setOptions({
    colors: ['#8080ff','#80ff80', '#ff8080']
});

var chart;

$(document).ready(function() {
   chart = new Highcharts.Chart({
      
	   chart: {
         renderTo: 'containerForRosterVsActualCharts',
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
         categories: <%=request.getAttribute("alLabel")%>
      },      
      yAxis: {
    	  
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
     
      series: [{
          name: 'Worked extra',
          data: <%=request.getAttribute("alVarianceHoursE")%>
    
       },{
           name: 'Worked Less',
           data: <%=request.getAttribute("alVarianceHoursL")%>
     
        }]
   });
});
</script>

<div id="containerForRosterVsActualCharts" style="height: 200px; width:95%"></div>
