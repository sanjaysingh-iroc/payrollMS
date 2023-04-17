<script type="text/javascript">

Highcharts.setOptions({
    colors: ['#00FF00','#FF6633']
});

var chart;
 
$(document).ready(function() {
	   chart = new Highcharts.Chart({
	      
		chart: { 
				renderTo: 'container_EmployeeTurnover',
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
	             ['Joined', <%=request.getAttribute("JOINING_COUNT")%> ],
	             ['Left', <%=request.getAttribute("LEAVING_COUNT")%>]
	         ]
	       }]
	   });
});
</script>

<div id="container_EmployeeTurnover" style="height: 200px; width:300px"></div>