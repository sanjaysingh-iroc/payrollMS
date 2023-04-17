


<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
	UtilityFunctions uF=new UtilityFunctions();
	String targetachievedPercent = (String) request.getAttribute("myTargetachievedPercent");
 	String targetremainPercent = (String) request.getAttribute("myTargetremainPercent");

%>
<script type="text/javascript">
var chart1234;
$(document).ready(function () {
//$(function () { 
  //  $('#containerforTargetDonutChart').highcharts({ 
    	chart1234 = new Highcharts.Chart({ 
        chart: {
        	renderTo: 'containerforMyTargetDonutChart',
        	type: 'pie', 
            options3d: {
				enabled: true,
                alpha: 180
            }
        },
        title: {
            text: ''
        },
        subtitle: {
            text: ''
        },
        plotOptions: {
            pie: {
            	innerSize: 130,
                depth: 45,
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series: [{
            name: 'Percentage',
            data: [
                ['Target Missed', <%=uF.showData(targetremainPercent,"0")%>],
                ['Target Achieved', <%=uF.showData(targetachievedPercent,"0")%>] 
            ]
        }]
    });
}); 

</script>

<div id="containerforMyTargetDonutChart"></div>