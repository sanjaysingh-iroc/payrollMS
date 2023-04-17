<%@page import="java.util.ArrayList"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>

<script type="text/javascript">

<%ArrayList alCostCenterNames = (ArrayList)request.getAttribute("alCostCenterNames");%>
<%ArrayList alCostCenterCnt = (ArrayList) request.getAttribute("alCostCenterCnt");%>

Highcharts.setOptions({
    colors: ['#ff8080','#80ff80', '#8080ff']
});

var chart;

$(document).ready(function() {
   chart = new Highcharts.Chart({
      
	   chart: {
         renderTo: 'CScontainer',
         defaultSeriesType: 'column',
       	 plotBorderWidth: 1
      },
      title: {
          text: 'Cost Center Details'
       },
      credits: {
          enabled: false
      },
      title: {
         text: ' '
      },
      xAxis: {
         categories: ['Cost Centres']
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
      
      series: [
    	  
    	  <% for(int i=0; i<alCostCenterNames.size(); i++) { 
    	  	System.out.println(alCostCenterNames.get(i));
    	  	System.out.println(alCostCenterCnt.get(i));
    	  %>
    	  {
    		  name: '<%=alCostCenterNames.get(i)%>', 
    		  data: [<%=alCostCenterCnt.get(i)%>]
    	  },
    	  <%}%>
    	  
       /*    name: 'CS1',
          data: [10]
    
       }, {
          name: 'CS2',
          data: [20]
       
       }, {
          name: 'CS3',
          data: [5] */
       
       
           ]
   });
});

</script>

<div id="CScontainer" style="height: 200px;"></div>