
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
	UtilityFunctions uF=new UtilityFunctions();
	String targetachievedPercent = (String) request.getAttribute("targetachievedPercent");
 	String targetremainPercent = (String) request.getAttribute("targetremainPercent");

%>
<script type="text/javascript">
var chartTeam;
$(document).ready(function () {
//$(function () { 
  //  $('#containerforTargetDonutChart').highcharts({ 
    	chartTeam = new Highcharts.Chart({ 
        chart: {
        	renderTo: 'containerforTargetDonutChart',
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
                depth: 45
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

<div id="containerforTargetDonutChart" style="height: 330px"></div>