


<script type="text/javascript">

Highcharts.setOptions({
    colors: ['#00FF00','#FF6633']
});

var chart;
 
$(document).ready(function() {
	   chart = new Highcharts.Chart({
	      
		chart: { 
				renderTo: 'containerForPieCharts',
	        	plotBackgroundColor: null,
	        	plotBorderWidth: null,
	        	plotShadow: false
		     },
     	credits: {
		         	enabled: false
		     },
		title: {
		   	  		text : '',
		       		floating: true
		     },
			tooltip: {
		   	   formatter: function() {
		           return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
		        }
		  	},
		 	legend: {
		        enabled: true
    		},
    		plotOptions: {
    	         pie: {
    	            allowPointSelect: true,
    	            cursor: 'pointer',
    	            dataLabels: {
    	               enabled: false
    	            },
    	            showInLegend: true
    	         }
    	      },
	      series: [{
	          type: 'pie',
	          name: 'Browser share',
	          data: [
	             ['Accept',4], <%-- <%=request.getAttribute("PRESENT_COUNT")%>  --%>
	             ['Reject', 3]  <%--  <%=request.getAttribute("ABSENT_COUNT")%> --%>
	         ]
	       }]
	   });
});
</script>

<div id="containerForPieCharts" style="height: 500px; width:100%"></div>