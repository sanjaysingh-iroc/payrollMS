<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<script type="text/javascript">
<%UtilityFunctions uF = new UtilityFunctions(); %>
Highcharts.setOptions({
    colors: ['#80ff80', '#8080ff','#ff8080']
});

var chart;

$(document).ready(function() {
   chart = new Highcharts.Chart({
      
	   chart: {
         renderTo: 'containerForCharts',
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
         categories: ['Exceptions', 'Reimbursements', 'Leaves']
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
      
      series: [{
          name: 'Pending for approval',
          data: [<%=uF.showData((String)request.getAttribute("EXCEP_WAITING_COUNT"),"0")%>,<%=uF.showData((String)request.getAttribute("REIMB_WAITING_COUNT"),"0")%>,<%=uF.showData((String)request.getAttribute("LEAVE_PENDING_COUNT"),"0")%>] 
		 
    
       }]
   });
});
</script>

<div id="containerForCharts" style="height: 200px; width:100%"></div>
