<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>
<script type="text/javascript" src="scripts/charts/exporting.js"></script>
<script type="text/javascript">

Highcharts.setOptions({
    colors: ['#ff8080','#80ff80', '#8080ff']
});

var chart;

$(document).ready(function() {
   chart = new Highcharts.Chart({
      
	   chart: {
         renderTo: 'containerForLeaveCharts',
         defaultSeriesType: 'bar',
       	 plotBorderWidth: 1
      },
      
      xAxis: {
         categories: [<%=request.getAttribute("TYPE")%>]
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
    	  bar: {
	          pointPadding: 0.2,
	          borderWidth: 0
	       }
      },           

      colors: ['#008000', '#FFA500', '#4682B4'],
      
      series: [{
          name: 'Approved',
          data: [<%=request.getAttribute("APPROVED")%>]
    
       }, {
          name: 'Pending',
          data: [<%=request.getAttribute("PENDING")%>]
       
       }, {
          name: 'Balance',
          data: [<%=request.getAttribute("BALANCE")%>]
           }]
   });
});
</script>

<div id="containerForLeaveCharts" style="height: 200px; width:100%"></div>
