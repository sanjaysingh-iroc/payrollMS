

<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>

	<%
	
	UtilityFunctions uF=new UtilityFunctions(); 
	
	 Map<String,String>  hmCandOfferRejected=(Map<String,String>)request.getAttribute("hmCandOfferRejected");
	 Map<String,String>  hmCandOfferAccepted=(Map<String,String>)request.getAttribute("hmCandOfferAccepted");
	 Map<String,String>  hmCandOfferUP=(Map<String,String>)request.getAttribute("hmCandOfferUP");
	 Map<String,String>  hmCandTotalSelected=(Map<String,String>)request.getAttribute("hmCandTotalSelected");
	
	String jobcode=(String)request.getAttribute("jobcode");

%>

<script type="text/javascript">

Highcharts.setOptions({
    colors: ['#00FF00','#FF6633']
});

var chart;
 
$(document).ready(function() {
	   chart = new Highcharts.Chart({
	      
		chart: { 
				renderTo: 'applicationFinalstats',
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
	          data:[
		             ['Rejected',2 ],
		             ['Shortlisted',2 ],
		             ['Finalisation',1]
	         ]
	       }]
	   });
});


Highcharts.setOptions({
    colors: ['#00FF00','#FF6633']
});

var chart;
 
$(document).ready(function() {
	   chart = new Highcharts.Chart({
	      
		chart: { 
				renderTo: 'offerFinalStats',
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
	
	             ['Rejected',<%= uF.parseToInt(uF.showData(hmCandOfferRejected.get(jobcode),"0")   ) %> ],
	             ['Offered',<%= uF.parseToInt(uF.showData(hmCandTotalSelected.get(jobcode),"0")   ) %>],
	             ['Accepted',<%= uF.parseToInt(uF.showData(hmCandOfferAccepted.get(jobcode),"0")   ) %>]
	         ]
	       }]
	   });
});
</script>

<div>

<div class="KPI"> 
                
				<p class="past close_div">JOB STATS </p>
				  <div class="content1">
					  <div class="holder">	
					  
					  <div id="applicationFinalstats" style="height: 200px; width:100%"></div>
					  <div id="offerFinalStats" style="height: 200px; width:100%">
					  
					  
					  </div>
					  </div>
					  </div>
					  

<!-- <table>

<tr>
<td> <div id="offerFinalStats" style="height: 200px; width:100%"></div></td>
<td>	<div id="applicationFinalstats" style="height: 200px; width:100%"></div> </td>
</tr>


</table> -->


</div>




















































